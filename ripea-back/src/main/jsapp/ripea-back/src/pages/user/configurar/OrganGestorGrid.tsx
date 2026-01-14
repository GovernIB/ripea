import {useTranslation} from "react-i18next";
import { useNavigate } from "react-router-dom";
import {useEffect, useMemo, useRef, useState} from "react";
import {GridPage, MuiDialog, useResourceApiService} from "reactlib";
import {CardData, CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Badge, IconButton, Divider} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import Load from "../../../components/Load.tsx";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";

const Node = (props: any) => {
    return (
        <Grid
            item
            sx={{
                backgroundColor: "white",
                color: "black",
                textAlign: "center",
                borderRadius: 2,
                fontWeight: "bold",
                boxShadow: 2,
                minWidth: 50,
                flexGrow: 0,
                paddingLeft: '0 !important',
                paddingTop: '0 !important',
                ...props,
            }}
        >
            {props.children}
        </Grid>
    );
};

const NodeGrup = (props: any) => {
    const { nodeKey, values, divider } = props;
    return (
        <Grid container item xs={12} direction="row" wrap="nowrap" justifyContent={"space-around"} alignItems="center" columnSpacing={1} rowSpacing={1}>
            {nodeKey && <Node backgroundColor="green" color="white">{`${nodeKey?.codi} - ${nodeKey.denominacioCooficial}`}</Node>}
            {(divider || (nodeKey && values)) && (
                <Grid item sx={{display: "flex", alignItems: "center", justifyContent: "center" }}>
                    <Divider orientation="horizontal" flexItem sx={{ borderColor: "black", borderWidth: 2, width: 40 }}/>
                </Grid>
            )}
            {values?.map((value:any) => <Node backgroundColor="orange" color="white">{`${value?.codi} - ${value.denominacioCooficial}`}</Node>)}
        </Grid>
    );
};

const useActions = (refresh?: () => void) => {
    const [prediccio, setPrediccio] = useState<any>();
    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
    } = useResourceApiService('organGestorResource');

    useEffect(() => {
        if (apiIsReady) {
            apiAction(undefined, {code: "DIR3_UPDATE"})
                .then((res) => {
                    setPrediccio(res)
                })
                .catch(() => {
                    setPrediccio(undefined)
                });
        }
    }, [apiIsReady]);

    const descargarPDF = async (element:HTMLElement) => {
        const canvas = await html2canvas(element, {
            scale: 2,
            useCORS: true,
        });

        const imgData = canvas.toDataURL("image/png");

        const pdf = new jsPDF("p", "mm", "a4");

        const pageWidth = pdf.internal.pageSize.getWidth();
        const pageHeight = pdf.internal.pageSize.getHeight();

        const imgWidth = pageWidth;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;

        let heightLeft = imgHeight;
        let position = 0;

        // Primera página
        pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;

        // Páginas siguientes
        while (heightLeft > 0) {
            position = heightLeft - imgHeight;
            pdf.addPage();
            pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
            heightLeft -= pageHeight;
        }

        pdf.save("Informe_actualitzacio_organs.pdf");
    };

    return {
        prediccio,
        descargarPDF,
    }
}

const useOrganGestorSyncDialog = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const ref = useRef();

    const handleOpen = () => {
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const {prediccio, descargarPDF} = useActions(handleClose);
    // const changed = useMemo(() => (
    //     prediccio?.unitatsVigents?.filter?.((unitat:any) => unitat.oldDenominacio!=unitat.denominacioCooficial)
    // ), [prediccio])
    const buttons :any[] = useMemo(() => [
        {
            value: 'download',
            text: t('common.download'),
            icon: 'download',
            componentProps: {
                variant: "outlined",
            }
        },
        {
            value: 'sync',
            text: t('page.organGestor.action.actualitzar.button'),
            icon: 'save',
            componentProps: {
                variant: "contained",
                color: "success",
            }
        },
        {
            value: 'close',
            text: t('common.cancel'),
            icon: 'close',
            componentProps: {
                variant: "outlined",
            }
        },
    ], [t])

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.organGestor.action.actualitzar.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={buttons}
            buttonCallback={(value :any) :void => {
                switch (value){
                    case 'download':
                        descargarPDF(ref?.current)
                        break;
                    case 'sync':
                        break;
                    case 'close':
                        handleClose();
                        break;
                }
            }}
        >
        <Load value={prediccio}>
        <Grid ref={ref} container item xs={12} direction="row" columnSpacing={1} rowSpacing={1}>
            {prediccio?.firstSincronization && <>
                <CardData title={t('page.organGestor.action.actualitzar.tabs.firstSync')} rowSpacing={2}>
                    {prediccio?.unitatsVigents?.map?.((unitat:any) => <NodeGrup nodeKey={unitat}/>)}
                </CardData>
            </>}
            {!prediccio?.firstSincronization && <>
                <CardData title={t('page.organGestor.action.actualitzar.tabs.split')} rowSpacing={2} hidden={prediccio?.splitMap?.empty}>
                    {prediccio?.splitMap?.map?.((map:any) => <NodeGrup nodeKey={map?.key} values={map?.values}/>)}
                </CardData>
                <CardData title={t('page.organGestor.action.actualitzar.tabs.merge')} rowSpacing={2} hidden={prediccio?.mergeMap?.empty}>
                    {prediccio?.mergeMap?.map?.((map:any) => <NodeGrup nodeKey={map?.key} values={map?.values}/>)}
                </CardData>
                <CardData title={t('page.organGestor.action.actualitzar.tabs.subst')} rowSpacing={2} hidden={prediccio?.substMap?.empty}>
                    {prediccio?.substMap?.map?.((map:any) => <NodeGrup nodeKey={map?.key} values={map?.values}/>)}
                </CardData>
                <CardData title={t('page.organGestor.action.actualitzar.tabs.change')} rowSpacing={2} hidden={prediccio?.unitatsVigents?.length == 0}>
                    {prediccio?.unitatsVigents?.map?.((unitat:any) => <>
                        <Grid container item xs={12} direction="row" wrap="nowrap" justifyContent={"space-around"} alignItems="center" columnSpacing={1} rowSpacing={1}>
                            <Node backgroundColor="green" color="white">{`${unitat?.codi} - ${unitat.oldDenominacio}`}</Node>
                            <Grid item sx={{display: "flex", alignItems: "center", justifyContent: "center" }}>
                                <Divider orientation="horizontal" flexItem sx={{ borderColor: "black", borderWidth: 2, width: 40 }}/>
                            </Grid>
                            <Node backgroundColor="orange" color="white">{`${unitat?.codi} - ${unitat.denominacioCooficial}`}</Node>
                        </Grid>
                    </>)}
                </CardData>
                <CardData title={t('page.organGestor.action.actualitzar.tabs.new')} rowSpacing={2} hidden={prediccio?.unitatsNew?.length == 0}>
                    {prediccio?.unitatsNew?.map?.((unitat:any) => <NodeGrup nodeKey={unitat}/>)}
                </CardData>
                <CardData title={t('page.organGestor.action.actualitzar.tabs.del')} rowSpacing={2} hidden={prediccio?.unitatsExtingides?.length == 0}>
                    {prediccio?.unitatsExtingides?.map?.((unitat:any) =>
                        <Grid container item xs={12} direction="row" wrap="nowrap" justifyContent={"space-around"} alignItems="center" columnSpacing={1} rowSpacing={1}>
                            <Node backgroundColor="red" color="white">{`${unitat?.codi} - ${unitat.denominacioCooficial}`}</Node>
                        </Grid>
                    )}
                </CardData>
            </>}
        </Grid>
        </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

// Filter
const OrganGestorFilterForm = () => {
    return <>
        <GridFormField xs={2} name="codi"/>
        <GridFormField xs={2} name="nom"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={2} name="estat"/>
        <Grid item xs={0.6}/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.eq('pare.id', data?.organGestor?.id),
        builder.eq('estat', `'${data?.estat}'`),
    );
}

const OrganGestorFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"organGestorResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <OrganGestorFilterForm/>
    </StyledMuiFilter>
}

// Grid
const OrganGestorForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi" disabled readOnly/>
        <GridFormField xs={12} name="nom" disabled readOnly/>
        <GridFormField xs={12} name="pare" disabled readOnly/>
        <GridFormField xs={12} name="cif" disabled readOnly/>
        <GridFormField xs={12} name="utilitzarCifPinbal"/>
        <GridFormField xs={12} name="permetreEnviamentPostal"/>
    </Grid>
}

const sortModel: any = [{field: 'nom', sort: 'asc'}]

const OrganGestorGrid = () => {
    const {t} = useTranslation();
    const navigate = useNavigate();
    const [springFilter, setSpringFilter] = useState<string>();
    const [treeView, setTreeView] = useState<boolean>(false);

    const {handleOpen, dialog} = useOrganGestorSyncDialog();
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
    ]

    const perspectives = useMemo(() => treeView?['PATH','COUNT_PERMISOS']:['COUNT_PERMISOS'], [treeView])
    const columns = useMemo(()=>[
        {
            field: 'codi',
            flex: 0.5,
            hidden: treeView
        },
        {
            field: 'nom',
            flex: 1,
            hidden: treeView
        },
        {
            field: 'pare',
            flex: 1,
            hidden: treeView
        },
        {
            field: 'cif',
            flex: 0.5,
        },
        {
            field: 'estat',
            flex: 0.5,
        },
        {
            filed: 'permis',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params:any) => <IconButton
                aria-label="key"
                color="inherit"
                title="Permisos"
                onClick={(e:any) => { e.stopPropagation(); navigate(`/organgestor/${params?.row?.id}/permis`); }}
            >
                <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                    <Icon>key</Icon>
                </Badge>
            </IconButton>
        }
    ]
        .filter((col:any)=>!col?.hidden), [treeView])

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.organs')}>
            <OrganGestorFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"organGestorResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.organGestor.title')}
                popupEditFormContent={<OrganGestorForm/>}
                columns={columns}
                filter={springFilter}
                perspectives={perspectives}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton
                            onClick={handleOpen}
                            icon={'cached'}
                            color={'primary'}>&nbsp;{t('page.organGestor.action.actualitzar.label')}</ToolbarButton>,
                    },
                    {
                        position: 3,
                        element: <ToolbarButton
                            icon={'visibility'}
                            variant={treeView ?"contained" :"outlined"}
                            onClick={()=>setTreeView(prev=>!prev)}
                            color={'primary'}>&nbsp;{t('page.organGestor.action.vista')}</ToolbarButton>,
                    },
                ]}

                paginationActive={!treeView}
                autoHeight={treeView}
                treeData={treeView}
                groupingColDef={{
                    headerName: t('page.contingut.grid.nom'),
                    flex: 1,
                    valueFormatter: (value: any, row: any) => row?.codi +" - "+ row?.nom,
                }}
                treeDataAdditionalRows={(_rows: any) => {
                    const additionalRows: any[] = [];
                        if (_rows!=null && treeView){
                            for (const row of _rows) {
                                for (const r of row?.path) {
                                    if (!additionalRows.map((b:any) => b.id).includes(r?.id)
                                        && !_rows.map((b:any) => b.id).includes(r?.id))
                                    {
                                        additionalRows.push(r)
                                    }
                                }
                            }
                        }
                    return additionalRows;
                }}
                getTreeDataPath={(row: any): string[] => {
                        return !!row?.pathName ?row?.pathName :[`${row.id}`];
                }}

                toolbarHideCreate
                toolbarHideRefresh
                popupEditFormI18nKeys={{
                    updateSuccess: 'page.organGestor.action.update.ok',
                }}
            />
        </CardPage>
        {dialog}
    </GridPage>
}
export default OrganGestorGrid;