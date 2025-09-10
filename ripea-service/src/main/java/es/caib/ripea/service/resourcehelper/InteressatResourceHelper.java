package es.caib.ripea.service.resourcehelper;

import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.service.base.helper.ObjectMappingHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ExpedientInteressatHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
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
import java.util.stream.Collectors;

import static com.sun.star.awt.ContainerWindowProvider.create;

@Component
@RequiredArgsConstructor
public class InteressatResourceHelper {

    private final InteressatResourceRepository interessatResourceRepository;
    private final InteressatRepository interessatRepository;
    private final ExpedientInteressatHelper expedientInteressatHelper;
    private final ConfigHelper configHelper;

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

    public String importarInteressats(Long expedientId, List<InteressatResource> interessats) {
        int numInteressatsImp = 0;
        int numInteressatsUpd = 0;

        if (interessats != null && !interessats.isEmpty()) {
            List<InteressatResourceEntity> interessatsActualsExp = interessatResourceRepository.findByExpedientId(expedientId);
            List<String> numDocList = interessatsActualsExp.stream().map(InteressatResourceEntity::getDocumentNum).collect(Collectors.toList());

            for (InteressatResource interessat : interessats) {
                if (!numDocList.contains(interessat.getDocumentNum())) {
                    interessat = create(interessat);
                    numInteressatsImp++;
                } else {
                    interessat = update(interessat);
                    numInteressatsUpd++;
                }

                if (interessat.getRepresentant() != null) {
                    InteressatResource representant = interessat.getRepresentantInfo();
                    representant.setRepresentat(ResourceReference.toResourceReference(interessat.getId()));
                    if (!numDocList.contains(representant.getDocumentNum())) {
                        create(representant);
                    } else {
                        update(representant);
                    }
                }
            }
        }

        return "S'han importat <b>" + numInteressatsImp + "</b> nous interessats, i <b>" + numInteressatsUpd + "</b> s'han actualitzat.";
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (Exception e) {
            throw e;
        }
    }

    public InteressatResource create(InteressatResource resource) {
        Long resultId;
        InteressatDto interessatDto = toInteressatDto(resource);
        InteressatEntity interessatExistent = interessatRepository.findByExpedientIdAndDocumentNum(
                resource.getExpedient().getId(),
                resource.getDocumentNum());
        //CREATE
        if(interessatExistent==null) {
            //Create representant
            if(resource.getRepresentat()!=null) {
                InteressatEntity interessat = expedientInteressatHelper.createRepresentantEntity(
                        resource.getExpedient().getId(),
                        resource.getRepresentat().getId(),
                        interessatDto, //Dades del representant
                        true, //propagarArxiu
                        PermissionEnumDto.WRITE,
                        configHelper.getRolActual(),
                        true); //comprovarAgafat
                resultId = interessat.getId();
            } else {
                InteressatEntity interessat = expedientInteressatHelper.createInteressatEntity(
                        resource.getExpedient().getId(),
                        interessatDto,
                        true, //propagarArxiu
                        PermissionEnumDto.WRITE,
                        configHelper.getRolActual(),
                        true); //comprovarAgafat
                resultId = interessat.getId();
            }
        } else { //UPDATE
            interessatDto.setId(interessatExistent.getId());
            InteressatEntity interessat = expedientInteressatHelper.updateInteressatRepresentantEntity(
                    resource.getExpedient().getId(),
                    resource.getRepresentat()!=null?resource.getRepresentat().getId():null, //ID del representat
                    interessatDto,
                    configHelper.getRolActual(),
                    true,  //comprovarAgafat
                    true); //propagarArxiu
            resultId = interessat.getId();
        }

        resource.setId(resultId);
        return resource;
    }

    public InteressatResource update(InteressatResource resource) throws ResourceNotFoundException {
        Long resultId;
        InteressatEntity interessatExistent = interessatRepository.findByExpedientIdAndDocumentNum(resource.getExpedient().getId(), resource.getDocumentNum());

        //Modificam un representant
        if (resource.getRepresentat()!=null) {

            if (interessatExistent!=null && !interessatExistent.getId().equals(resource.getId())) {
                //Si el interessat que hem trobat amb el mateix NIF, no és el representat, per tant ja no es validarà amb el metode update
                if (interessatExistent.getRepresentant()==null || !interessatExistent.getRepresentant().getDocumentNum().equals(resource.getDocumentNum())) {
                    //En aquest cas canviam el ID, i rl que es modificará será el interessat
                    resource.setId(interessatExistent.getId());
                }
            }

            //A la funció de update, ja es comprova que no es modifiqui amb el mateix ID que el representat (ValidationException)
            InteressatDto interessat = expedientInteressatHelper.update(
                    resource.getExpedient().getId(),
                    resource.getRepresentat().getId(),
                    toInteressatDto(resource),
                    configHelper.getRolActual(),
                    true,
                    true);
            resultId = interessat.getId();
        } else {

            /**
             * Modificam un interessat. Cas 1: Hem introduit el mateix document que el seu representant... Validation exception
             */
            if (resource.getRepresentant()!=null && interessatExistent!=null &&
                    resource.getRepresentant().getId()!=null &&
                    resource.getRepresentant().getId().equals(interessatExistent.getId())) {
                //Modificam un interessat, de tal manera que quedará amb el mateix document que el seu representant
                //La funció update s'encarrega de fer la validació
                InteressatDto interessat = expedientInteressatHelper.update(
                        resource.getExpedient().getId(),
                        null,
                        toInteressatDto(resource),
                        configHelper.getRolActual(),
                        true,
                        true);
                resultId = interessat.getId();
            } else {

                /**
                 * Modificam un interessat. Cas 2: Hem introduit el mateix document que un altre interessat del expedient
                 */
                if (interessatExistent!=null && !interessatExistent.getId().equals(resource.getId())) {
                    resource.setId(interessatExistent.getId());
                } else {
                    /**
                     * Modificam un interessat. Cas 3: El numero de document no esta repetit per l'expedient
                     */
                }

                InteressatDto interessat = expedientInteressatHelper.update(
                        resource.getExpedient().getId(),
                        null,
                        toInteressatDto(resource),
                        configHelper.getRolActual(),
                        true,
                        true);
                resultId = interessat.getId();
            }
        }

        resource.setId(resultId);
        return resource;
    }

    private InteressatDto toInteressatDto(InteressatResource resource) {
        switch (resource.getTipus()) {
            case InteressatAdministracioEntity:
                InteressatAdministracioDto interessatAdmDto = new InteressatAdministracioDto();
                interessatAdmDto.setOrganCodi(resource.getOrganCodi());
                interessatAdmDto.setOrganNom(resource.getOrganNom());
                setDadesComunsInteressat(resource, interessatAdmDto);
                return interessatAdmDto;
            case InteressatPersonaFisicaEntity:
                InteressatPersonaFisicaDto interessatFisDto = new InteressatPersonaFisicaDto();
                interessatFisDto.setNom(resource.getNom());
                interessatFisDto.setLlinatge1(resource.getLlinatge1());
                interessatFisDto.setLlinatge2(resource.getLlinatge2());
                setDadesComunsInteressat(resource, interessatFisDto);
                return interessatFisDto;
            case InteressatPersonaJuridicaEntity:
                InteressatPersonaJuridicaDto interessatJurDto = new InteressatPersonaJuridicaDto();
                interessatJurDto.setRaoSocial(resource.getRaoSocial());
                setDadesComunsInteressat(resource, interessatJurDto);
                return interessatJurDto;
        }
        return null;
    }

    private void setDadesComunsInteressat(InteressatResource resource, InteressatDto interessatDto) {
        //Controlar que no estam introduint un interessat repetit
        interessatDto.setId(resource.getId());
        interessatDto.setDocumentTipus(resource.getDocumentTipus());
        interessatDto.setDocumentNum(resource.getDocumentNum());
        interessatDto.setPais(resource.getPais());
        interessatDto.setProvincia(resource.getProvincia());
        interessatDto.setMunicipi(resource.getMunicipi());
        interessatDto.setCodiPostal(resource.getCodiPostal());
        //ADRESSA NORMALITZADA
        interessatDto.setAdresa(resource.getAdresa());
        if (resource.getAdressaTipus()==null || EntregaPostalTipusEnum.SENSE_NORMALITZAR.equals(resource.getAdressaTipus())) {
        	interessatDto.setAdressaTipus(EntregaPostalTipusEnum.SENSE_NORMALITZAR);
        	interessatDto.setAdressaTipusVia(null);
        	interessatDto.setAdressaNumCasa(null);
        	interessatDto.setAdresaQualificador(null);
        	interessatDto.setAdresaPuntKm(null);
        	interessatDto.setAdresaApartatCorreus(null);
        	interessatDto.setAdresaPortal(null);
        	interessatDto.setAdresaEscala(null);
        	interessatDto.setAdresaPlanta(null);
        	interessatDto.setAdresaPorta(null);
        	interessatDto.setAdresaBloc(null);
        	interessatDto.setAdresaComplement(null);
        	interessatDto.setAdresaPoblacio(null);
        } else {
        	interessatDto.setAdressaTipus(resource.getAdressaTipus());
        	interessatDto.setAdressaTipusVia(resource.getAdressaTipusVia());
        	interessatDto.setAdressaNumCasa(resource.getAdressaNumCasa());
        	interessatDto.setAdresaQualificador(resource.getAdresaQualificador());
        	interessatDto.setAdresaPuntKm(resource.getAdresaPuntKm());
        	interessatDto.setAdresaApartatCorreus(resource.getAdresaApartatCorreus());
        	interessatDto.setAdresaPortal(resource.getAdresaPortal());
        	interessatDto.setAdresaEscala(resource.getAdresaEscala());
        	interessatDto.setAdresaPlanta(resource.getAdresaPlanta());
        	interessatDto.setAdresaPorta(resource.getAdresaPorta());
        	interessatDto.setAdresaBloc(resource.getAdresaBloc());
        	interessatDto.setAdresaComplement(resource.getAdresaComplement());
        	interessatDto.setAdresaPoblacio(resource.getAdresaPoblacio());
        }
        
        interessatDto.setEmail(resource.getEmail());
        interessatDto.setTelefon(resource.getTelefon());
        interessatDto.setObservacions(resource.getObservacions());
        interessatDto.setPreferenciaIdioma(resource.getPreferenciaIdioma());
        interessatDto.setEntregaDeh(resource.getEntregaDeh());
        interessatDto.setEntregaDehObligat(resource.getEntregaDehObligat());
    }
}
