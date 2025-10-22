import React, {useEffect, useMemo, useRef, useState} from "react";
import {Icon} from "@mui/material";
import { GridPage, useMuiDataGridApiRef, useResourceApiService } from 'reactlib';
import {useTranslation} from "react-i18next";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import useInteressatActions, {useActions} from "./details/InteressatActions.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import useImport from "./actions/Import.tsx";
import {useActions as useExpedientActions} from "../expedient/details/CommonActions.tsx"
import {InteressatsGridForm} from "./InteressatsGridForm.tsx";
import {MenuActionButton} from "../../components/MenuButton.tsx";
import {useUserSession} from "../../components/Session.tsx";
import useInteressatDetail from "./details/InteressatDetail.tsx";
import useCreateGrup from "./groups/actions/CreateGrup.tsx";

const perspectives = ['REPRESENTANT', 'GRUPS']
const sortModel:any = [{field: 'id', sort: 'asc'}]

interface DetailGridProps {
    entity: any,
    num: number,
    onRowCountChange?: (number: number) => void,
}

export const useInteressatsAmbGrups = (expedientId: number) => {

  const {
    isReady: apiGrupReady,
    find: apiFindGrups,
  } = useResourceApiService("interessatGrupResource");

  const [grups, setGrups] = useState<any[]>([]);

  const commonFilter = useMemo(() => builder.and(
      builder.or(
          builder.eq('expedient.id', expedientId)
      )
  ), [expedientId]);
  
  const refresh = () => {
    if (expedientId) {
      apiFindGrups({
        filter: commonFilter,
        unpaged: true,
		perspectives: ["INTERESSATS"],
      })
        .then((res) => setGrups(res.rows))
        .catch(() => setGrups([]));
    }
  };

  useEffect(() => {
    if (apiGrupReady) refresh();
  }, [apiGrupReady, expedientId]);
  
  return {
    isReady: apiGrupReady,
	setGrups,
    grups,
    refresh,
  };
};

const InteressatsGrid: React.FC<DetailGridProps> = (props: DetailGridProps) => {
    const {entity, num, onRowCountChange} = props
    const { t } = useTranslation();
    const { value: user } = useUserSession()
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
	const idToOriginalIdRef = useRef<Record<string, any>>({});
	
	const { isReady, setGrups, grups, refresh: refreshInteressats } = useInteressatsAmbGrups(entity?.id);
	
	
	const getTipusLabel = (row: any) => {
	    if (!row) return '';

	    switch (row.tipus) {
	        case 'InteressatPersonaFisicaEntity':
	            return t('page.interessat.grid.tipus.personaFisica'); // Persona física
	        case 'InteressatPersonaJuridicaEntity':
	            return t('page.interessat.grid.tipus.personaJuridica'); // Persona jurídica
	        case 'InteressatAdministracioEntity':
	            return t('page.interessat.grid.tipus.administrador'); // Grupo
	        default:
	            return row.tipus || '';
	    }
	};
	
    const columns = [
        {
            field: 'documentNum',
            flex: 0.4,
        },
        {
            field: 'nomComplet',// organNom
            flex: 1.0,
            valueFormatter: (value: any, row:any) => value ?? row?.organNom,
            renderCell: (params:any) => <>
                {params?.formattedValue}
                {!params?.row?.isGroup && !params?.row?.arxiuPropagat &&
                    <Icon title={t('page.contingut.alert.guardarPendent')} color={'error'}>warning</Icon>}
            </>
        },
        {
            field: 'representant',
            flex: 1.0,
            renderCell: (params:any) => <>
                {params?.formattedValue}
                {params?.row?.representant && !params?.row?.representantInfo?.arxiuPropagat &&
                    <Icon title={t('page.contingut.alert.guardarPendent')} color={'error'}>warning</Icon>}
            </>,
        },
    ];

    const apiRef = useMuiDataGridApiRef()

    const refresh = ()=> {
		refreshInteressats();
        apiRef?.current?.refresh()
    }

    const {actions, createActions, components} = useInteressatActions(entity, refresh)
    const {exportar} = useActions(refresh);
    const {excelInteressats} = useExpedientActions(refresh);
    const {handleShow: handleImport, content: contentImport} = useImport(entity, refresh);
	const {handleShow: handleCreateGrup, content: contentGrup} = useCreateGrup(entity, refresh, setGrups);
    const {handleOpen, dialog} = useInteressatDetail();

    return <>
        <StyledMuiGrid
            resourceName="interessatResource"
            popupEditFormDialogResourceTitle={t('page.interessat.title')}
            columns={columns}
            paginationActive={false}
            autoHeight
            apiRef={apiRef}
            filter={builder.and(
                builder.eq('expedient.id', entity?.id),
                builder.eq('esRepresentant', false)
            )}
            staticSortModel={sortModel}
            perspectives={perspectives}
            // disableColumnSorting
            popupEditCreateActive
            popupEditFormContent={<InteressatsGridForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
                esRepresentant: false,
            }}
            rowAdditionalActions={actions}
            onRowCountChange={onRowCountChange}
            toolbarCreateTitle={t('page.interessat.action.new.label')}
            toolbarHideCreate={user?.sessionScope?.isMostrarImportacio || !entity?.potModificar}
            toolbarHideQuickFilter={false}
            toolbarHideRefresh
            selectionActive
            onRowSelectionModelChange={(newSelection) => {
                // console.log('Selection changed:', newSelection);
                setSelectedRows([...newSelection]);
            }}
            toolbarElementsWithPositions={[
                {
                    position: 0,
                    element: <ToolbarButton icon={'upload'}
                                            onClick={()=> {
												const originalIds = selectedRows.map(id => idToOriginalIdRef.current[id] ?? id);
												exportar(originalIds, entity)
											}}
                                            disabled={selectedRows?.length==0}
                    >{t('page.interessat.action.exportar.label')}</ToolbarButton>
                },
                {
                    position: 0,
                    element: <ToolbarButton icon={'download'}
                                            onClick={()=>handleImport()}
                                            hidden={!entity?.potModificar}
                    >{t('page.interessat.action.importar.label')}</ToolbarButton>
                },
				{
					position: 0,
				    element: <ToolbarButton icon={'groups'}
				    						onClick={()=>handleCreateGrup()}
				                            hidden={!entity?.potModificar}
                   >{t('page.interessat.grup.action.new.label')}</ToolbarButton>
				},
                {
                    position: 0,
                    element: <ToolbarButton icon={'description'}
                                            color={'success'}
                                            variant={'contained'}
                                            title={t('page.expedient.action.excelInteressats.title')}
                                            onClick={()=>excelInteressats(entity?.id)}
                                            // disabled={selectedRows?.length==0}
                                            hidden={!entity?.potModificar || !num}
                    />,
                },
                {
                    position: 3,
                    element: <MenuActionButton
                        id={'createInteressats'}
                        hidden={!user?.sessionScope?.isMostrarImportacio || !entity?.potModificar}
                        buttonLabel={t('page.interessat.action.new.label')}
                        buttonProps={{
                            startIcon: <Icon>add</Icon>,
                            variant: "outlined",
                            sx: { borderRadius: '4px', minWidth: '20px', minHeight: '32px', py: 0 }
                        }}
                        actions={createActions}
                    />,
                }
            ]}
			groupingColDef={{
			  disableColumnMenu: true,
			  headerName: t('page.interessat.grid.tipus.label'),
			  flex: 1,
			  valueGetter: (value: any, row: any) => getTipusLabel(row),
			}}
			treeData
			isGroupExpandedByDefault={() => true}
			treeDataAdditionalRows={(interessats) => {
			
			  if (!interessats || !Array.isArray(interessats)) return [];

			  // Grupos como filas raíz
			  const grupRows = (grups ?? []).map(g => ({
			    id: `grup_${g.id}`,      // id único para grupos
				_originalId: g.id,
			    isGroup: true,
				_originalNom: g.nom,
				interessats: g.interessats,
			    tipus: g.nom,
			  }));

			  // Interesados duplicados por grupo
			  const interessatRows = interessats.flatMap(int =>
			    int.grups.map(g => ({
			      ...int,
			      id: g ? `${int.id}_grup_${g.id}` : `${int.id}_nogrup`, // id único
			      parentId: g ? `grup_${g.id}` : null,
			      _originalId: int.id,  // guardamos id original para llamadas a API
			    }))
			  );
			  
			  const allRows = [...grupRows, ...interessatRows];
			  
			  // Construimos el mapa de id => _originalId
			  const map: Record<string, any> = {};
			  allRows.forEach(r => { map[r.id] = r._originalId; });
			  idToOriginalIdRef.current = map;
			  
			  return allRows;;
			}}
			
			getTreeDataPath={(row: any) => {
			  if (row.isGroup) return [row._originalNom]; // ruta única para grupo
			  if (row.parentId) {
			    const grup = grups.find(g => `grup_${g.id}` === row.parentId);
			    return [grup?.nom ?? 'Sense grup', `${row.nomComplet}-${row._originalId}`];
			  }

			  //const isInAnyGroup = grups.some(g => g.interessats?.some(i => i.id === row.id));
			 			 
			   //if (!isInAnyGroup) {
			     return [row.nomComplet + '-' + row._originalId];
			   //}

			   // Si está en algún grupo, no devolvemos path (ya se mostrará bajo el gruo
			   //return []; 
			}}


			loading={!isReady}
			onRefresh={refreshInteressats}
            onRowClick={(params: any) => {
				const row = params?.row;
				if (!row.isGroup) {
					handleOpen(params?.row?.id, params?.row);
				}
			}}
			isRowSelectable={(row: any) => {
			  return row?.id;
			}}
			isrowcl
            popupEditFormI18nKeys={{
                createSuccess: 'page.interessat.action.new.ok',
                updateSuccess: 'page.interessat.action.update.ok',
            }}
        />
        {dialog}
        {contentImport}
		{contentGrup}
        {components}
    </>
}

export default InteressatsGrid;