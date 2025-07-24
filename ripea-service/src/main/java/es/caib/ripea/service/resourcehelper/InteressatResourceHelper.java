package es.caib.ripea.service.resourcehelper;

import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.InteressatResource;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InteressatResourceHelper {

    private final InteressatResourceRepository interessatResourceRepository;

    public List<InteressatResource> extreureInteressatsExcel(InputStream excel) {
        List<InteressatResource> interessatsExcel = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(excel)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) return interessatsExcel;

            for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;

                // Columna 0: tipus interessat
                Cell tipusCell = row.getCell(0);

                if (tipusCell == null || tipusCell.getCellType() != Cell.CELL_TYPE_STRING) continue;

                InteressatResource interessatResource = new InteressatResource();
                String tipusInteressat = tipusCell.getStringCellValue().trim();
                switch (tipusInteressat) {
                    case "PERSONA_FISICA":
                        interessatResource.setTipus(InteressatTipusEnum.InteressatPersonaFisicaEntity);
                        break;
                    case "PERSONA_JURIDICA":
                        interessatResource.setTipus(InteressatTipusEnum.InteressatPersonaJuridicaEntity);
                        break;
                    case "ADMINISTRACIO":
                        interessatResource.setTipus(InteressatTipusEnum.InteressatAdministracioEntity);
                        break;
                }

                // Columna 1: tipus document identificació
                Cell docTipusCell = row.getCell(1);
                if (docTipusCell != null && docTipusCell.getCellType() == Cell.CELL_TYPE_STRING) {
                    InteressatDocumentTipusEnumDto documentTipus = parseEnum(docTipusCell.getStringCellValue().trim(), InteressatDocumentTipusEnumDto.class);
                    interessatResource.setDocumentTipus(documentTipus);
                }

                // Columna 2 endavant
                for (int col = 2; col <= 18; col++) {
                    Cell cell = row.getCell(col);
                    if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) continue;

                    String value = cell.getCellType() == Cell.CELL_TYPE_STRING
                            ? cell.getStringCellValue().trim()
                            : String.valueOf((int) cell.getNumericCellValue());

                    switch (col) {
                        case 2:
                            interessatResource.setDocumentNum(value);
                            break;
                        case 3:
                            interessatResource.setNom(value);
                            break;
                        case 4:
                            interessatResource.setLlinatge1(value);
                            break;
                        case 5:
                            interessatResource.setLlinatge2(value);
                            break;
                        case 6:
                            interessatResource.setRaoSocial(value);
                            break;
                        case 7:
                            interessatResource.setOrganCodi(value);
                            break;
                        case 8:
                            interessatResource.setOrganNom(value);
                            break;
                        case 9:
                            interessatResource.setAmbOficinaSir(Boolean.parseBoolean(value));
                            break;
                        case 10:
                            interessatResource.setPais(value);
                            break;
                        case 11:
                            interessatResource.setProvincia(value);
                            break;
                        case 12:
                            interessatResource.setMunicipi(value);
                            break;
                        case 13:
                            interessatResource.setAdresa(value);
                            break;
                        case 14:
                            interessatResource.setCodiPostal(value);
                            break;
                        case 15:
                            interessatResource.setEmail(value);
                            break;
                        case 16:
                            interessatResource.setTelefon(value);
                            break;
                        case 17:
                            interessatResource.setObservacions(value);
                            break;
                        case 18:
                            InteressatIdiomaEnumDto idioma = parseEnum(value, InteressatIdiomaEnumDto.class);
                            interessatResource.setPreferenciaIdioma(idioma);
                            break;
                    }
                }

                interessatsExcel.add(interessatResource);
            }

        } catch (Exception e) {
            throw new RuntimeException("Hi ha hagut un error recuperant la informació dels interessats del excel", e);
        }

        return interessatsExcel;
    }

    public String importarInteressats(Long expedientId, String rolActual, List<InteressatResource> interessats) {
        int numInteressatsUpd = 0;
        int numInteressatsIns = 0;
        Map<String, String> errorsInteressats = new HashMap<String, String>();

//        if (interessats != null && !interessats.isEmpty()) {
//
//            //Recuperam tots els InteressatDto del expedient, siguin interessats arrel o representants.
//            List<InteressatResourceEntity> interessatsActualsExp = interessatResourceRepository.findByExpedientId(expedientId);
//
//            //Recorrem els interessats del JSON que s'ha importat
//            for (InteressatResource interessat : interessats) {
//                InteressatResourceEntity interessatProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getDocumentNum());
//                if (interessatProcessar == null) {
//                    //El create, crea el interessat associat al expedient, sense FK cap a representant, i amb es_representant=false
//                    //És a dir, un interessat arrel del expedient.
//
//                    InteressatDto interessatCreatDto = create(expedientId, interessat, true, PermissionEnumDto.WRITE, rolActual, true);
//                    interessatProcessar = interessatResourceRepository.getOne(interessatCreatDto.getId());
//                    interessatsActualsExp.add(interessatProcessar);
//                    numInteressatsIns++;
//
//                } else {
//                    //El merge no toca ni la FK cap a representant, ni l'atribut es_representant
//                    //per tant si era interessat haurà actualitzat el interessat, i si era representant, el representant.
//                    interessatProcessar = mergeInteressat(interessatProcessar.getId(), interessat);
//                    numInteressatsUpd++;
//                }
//
//                if (interessat.getRepresentant() != null) {
//                    //Si el representant amb numDoc no existeix al expedient (sigui com a representant o com a interessat), es crea com a nou interessat
//                    InteressatResourceEntity representantProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getRepresentant().getDocumentNum());
//                    if (representantProcessar == null) {
//
//                        InteressatDto representantCreatDto = create(expedientId, interessat.getRepresentant(), true, PermissionEnumDto.WRITE, rolActual, true);
//                        representantProcessar = interessatResourceRepository.getOne(representantCreatDto.getId());
//                        representantProcessar.updateEsRepresentant(true);
//
//                    } else {
//                        representantProcessar = mergeInteressat(representantProcessar.getId(), interessat.getRepresentant());
//                    }
//
//                    //Ara tenim el representant actualitzat o creat, pero encara no apunta al interessat que estam important
//                    interessatProcessar.setRepresentant(representantProcessar);
//                }
//            }
//        }

        String resultatStr = "S'han importat <b>" + numInteressatsIns + "</b> nous interessats, i <b>" + numInteressatsUpd + "</b> s'han actualitzat.";
        if (errorsInteressats.size() > 0) {
            resultatStr += "<br/>Els seguents interessats no s'han pogut importar:";
            for (Map.Entry<String, String> entry : errorsInteressats.entrySet()) {
                resultatStr += "<br/> - " + entry.getKey() + ": " + entry.getValue();
            }
        }
        return resultatStr;
    }

    private InteressatResourceEntity getInteressatActualExpedientByDocNum(List<InteressatResourceEntity> interessatsActualsExp, String docNum) {
        if (interessatsActualsExp!=null) {
            for (InteressatResourceEntity interessatExistent : interessatsActualsExp) {
                if (interessatExistent.getDocumentNum().equalsIgnoreCase(docNum)) {
                    return interessatExistent;
                }
            }
        }
        return null;
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (Exception e) {
            throw e;
        }
    }
}
