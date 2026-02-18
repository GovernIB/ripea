package es.caib.ripea.service.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.service.LogService;
import es.caib.ripea.service.intf.utils.DateUtil;
import es.caib.ripea.service.intf.utils.Utils;

@Service
public class LogServiceImpl implements LogService {

    @Autowired private ConfigHelper configHelper;
    @Autowired private ContingutHelper contingutHelper;
    private static final Long maxNLinies = 10000L;
    private static final Long minNLinies = 100L;
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);
	
	@Override
	public List<FitxerInfo> llistarFitxers() {
		
        var directoriPath = configHelper.getConfig("es.caib.ripea.plugin.fitxer.logs.path");
        
        if (!Utils.hasValue(directoriPath)) {
            return new ArrayList<>();
        }
        
        List<FitxerInfo> fitxers = new ArrayList<>();
        try (Stream<Path> paths = Files.list(Paths.get(directoriPath))) {
            paths.filter(Files::isRegularFile).forEach(f -> {
                var file = f.toFile();
                try {
                    var attr = Files.readAttributes(f, BasicFileAttributes.class);
                    Date dataCreacio = new Date(attr.creationTime().toMillis());
                    Date dataModificacio = new Date(attr.lastModifiedTime().toMillis());
                    var mida = file.length();
                    var fitxer = new FitxerInfo().nom(file.getName())
                                                     .mida(mida)
                                                     .dataCreacio(DateUtil.toOffsetDateTime(dataCreacio))
                                                     .dataModificacio(DateUtil.toOffsetDateTime(dataModificacio));
                    fitxers.add(fitxer);
                } catch (Exception ex) {
                	logger.error("Errror obtenint la info del fitxer " + f.getFileName(), ex);
                }
            });
        } catch (Exception ex) {
        	logger.error("Error generant la info dels fitxers pel directori " + directoriPath, ex);
        }
        return fitxers;
	}

	@Override
	public FitxerContingut getFitxerByNom(String nom) {
        try {
        	
            var directoriPath = configHelper.getConfig("es.caib.ripea.plugin.fitxer.logs.path");
            if (!Utils.hasValue(directoriPath)) {
                return new FitxerContingut();
            }
            
            var filePath = Paths.get(directoriPath, nom);
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return new FitxerContingut();
            }
            
            var file = filePath.toFile();
            var attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            
            Date dataCreacio = new Date(attr.creationTime().toMillis());
            Date dataModificacio = new Date(attr.lastModifiedTime().toMillis());
            
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			FitxerDto fitxer = new FitxerDto();
			fitxer.setContingut(Files.readAllBytes(filePath));
			contingutHelper.crearNovaEntrada(nom + ".zip", fitxer, zos);
			zos.close();
			
			byte[] contingut = baos.toByteArray();
			
            return new FitxerContingut().contingut(contingut)
                                            .mimeType("application/zip")
                                            .nom(file.getName())
                                            .dataCreacio(DateUtil.toOffsetDateTime(dataCreacio))
                                            .dataModificacio(DateUtil.toOffsetDateTime(dataModificacio))
                                            .mida((long)contingut.length);
        } catch (IOException ex) {
        	logger.error("Error reading file content for " + nom, ex);
            return new FitxerContingut();
        }
	}

	@Override
	public void tailLogFile(String filePath) {
		
        var directoriPath = configHelper.getConfig("es.caib.ripea.plugin.fitxer.logs.path");
        if (!Utils.hasValue(directoriPath)) {
        	logger.error("[LogService.tailLogFile] No s'ha especificat valor a la propietat \"es.caib.ripea.plugin.fitxer.logs.path\"");
            return;
        }
        
        var path = Paths.get(directoriPath, filePath);
        new Thread(() -> {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                reader.skip(Files.size(path));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        queue.put(line);
                    } else {
                        // Sleep for a short time to avoid busy waiting
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                }

            } catch (IOException e) {
            	logger.error("[LogService.tailLogFile] IOException llegint el fitxer de log: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("[LogService.tailLogFile] Thread interrupted: " + e.getMessage());
            }
        }).start();
	}

	@Override
	public BlockingQueue<String> getQueue() {
		return queue;
	}

	@Override
	public List<String> readLastNLines(String nomFitxer, Long nLinies) {
		
        try {
        	
        	if (!Utils.hasValue(nomFitxer) || nLinies == null) {
        		logger.error("[LogService.readLastNLines] Parametres incorrectes, nomFitxer " + nomFitxer + " nLinies" + nLinies);
                return new ArrayList<>();
            }
        	
            var directoriPath = configHelper.getConfig("es.caib.ripea.plugin.fitxer.logs.path");
            if (!Utils.hasValue(nomFitxer)) {
            	logger.error("[LogService.nomFitxer] No s'ha especificat valor a la propietat \"es.caib.ripea.plugin.fitxer.logs.path\"");
                return new ArrayList<>();
            }
            
            if (nLinies > maxNLinies) {
                nLinies = maxNLinies;
            } else if (nLinies < minNLinies) {
                nLinies = minNLinies;
            }
            var path = Paths.get(directoriPath, nomFitxer);
            try (var file = new RandomAccessFile(path.toFile(), "r")) {
                var fileLength = file.length();
                LinkedList<String> lines = new LinkedList<>();
                var pointer = fileLength - 1;
                var currentLine = new StringBuilder();
                char ch;
                while (pointer >= 0 && lines.size() < nLinies) {
                    file.seek(pointer);
                    ch = (char) file.readByte();
                    if (ch == '\n') {
                        if (currentLine.length() > 0) {
                            lines.addFirst(currentLine.reverse().toString());
                            currentLine.setLength(0);
                        }
                    } else {
                        currentLine.append(ch);
                    }
                    pointer--;
                }
                // Add the last line if present
                if (currentLine.length() > 0) {
                    lines.addFirst(currentLine.reverse().toString());
                }
                return lines;
            }
        } catch (Exception ex) {
        	logger.error("[LogService.readLastNLines] Error no controlat", ex);
            return new ArrayList<>();
        }
	}

}