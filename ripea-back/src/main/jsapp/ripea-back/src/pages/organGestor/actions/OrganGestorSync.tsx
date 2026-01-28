import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import {Grid, Divider, Typography, Alert} from "@mui/material";
import {useMemo, useRef, useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {CardData} from "../../../components/CardData.tsx";
import Load from "../../../components/Load.tsx";
import {useTranslation} from "react-i18next";

/*
He aplicado un parche en OrganGestorGrid.tsx para que Node no haga spread de todas las props dentro de sx
(ahí suele estar la causa: props no-CSS terminan en sx y MUI falla al procesarlas,
lo que puede producir errores extraños como lectura de align).
*/
const Node = (props: any) => {
    const { children, backgroundColor, color, sx: sxProp } = props;
    return (
        <Grid
            item
            sx={{
                backgroundColor: backgroundColor ?? "white",
                color: color ?? "black",
                textAlign: "center",
                borderRadius: 2,
                fontWeight: "bold",
                boxShadow: 2,
                minWidth: 50,
                flexGrow: 0,
                paddingLeft: '0 !important',
                paddingTop: '0 !important',
                ...(sxProp || {}),
            }}
        >
            {children}
        </Grid>
    );
};

const NodeGrup = (props: any) => {
    const { nodeKey, values, divider } = props;
    return (
        <Grid container item xs={12} direction="row" wrap="nowrap" justifyContent={"space-around"} alignItems="center" columnSpacing={1} rowSpacing={1}>
            {nodeKey && <Node backgroundColor="#d6e9c6" color="black" minWidth="40% !important">
                <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${nodeKey?.codi} - ${nodeKey.denominacioCooficial}`}</Typography>
            </Node>
            }
            {(divider || (nodeKey && values)) && (
                <Grid item sx={{display: "flex", alignItems: "center", justifyContent: "center" }}>
                    <Divider orientation="horizontal" flexItem sx={{ borderColor: "black", borderWidth: 2, width: 40 }}/>
                </Grid>
            )}
            {values?.map((value:any) => <Node backgroundColor="#faebcc" color="black" minWidth="40% !important">
                <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${value?.codi} - ${value.denominacioCooficial}`}</Typography>
            </Node>)
            }
        </Grid>
    );
};

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation()
    const [prediccio, setPrediccio] = useState<any>();

    const {
        artifactAction: apiAction,
    } = useResourceApiService('organGestorResource');
    const {temporalMessageShow} = useBaseAppContext();

    const getPrediccio = () => {
        setPrediccio(undefined)
        apiAction(undefined, {code: "DIR3_PREDICT"})
            .then((res) => {
                setPrediccio(res)
            })
            .catch((error) => {
                if (error?.message) {
                    temporalMessageShow(null, error?.message, 'error');
                }
            });
    };
    const update = () => {
        setPrediccio(undefined)
        apiAction(undefined, {code: "DIR3_UPDATE"})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.organGestor.action.actualitzar.ok'), 'success');
            })
            .catch((error) => {
                if (error?.message) {
                    temporalMessageShow(null, error?.message, 'error');
                }
            });
    };

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
        getPrediccio,
        descargarPDF,
        update,
    }
}

export const useOrganGestorSyncDialog = () => {

    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const ref = useRef();

    const handleOpen = () => {
        getPrediccio();
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const {prediccio, getPrediccio, descargarPDF, update} = useActions(handleClose);
    // const changed = useMemo(() => (
    //     prediccio?.unitatsVigents?.filter?.((unitat:any) => unitat.oldDenominacio!=unitat.denominacioCooficial)
    // ), [prediccio])
    const buttons :any[] = useMemo(() => [
        {
            value: 'download',
            text: t('page.organGestor.action.pdf'),
            icon: 'download',
            componentProps: {
                variant: "outlined",
            },
            hidden: prediccio?.noCanvis,
        },
        {
            value: 'close',
            text: t('common.cancel'),
            icon: 'close',
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
                color: "primary",
            },
            hidden: prediccio?.noCanvis,
        },
    ].filter((button:any)=>!button?.hidden), [t, prediccio])

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
                        update()
                        break;
                    case 'close':
                        handleClose();
                        break;
                }
            }}
        >
            <Load value={prediccio}>
                <Grid ref={ref} container item xs={12} direction="row" columnSpacing={1} rowSpacing={1}>
                    {prediccio?.noCanvis ?<>
                        <Grid item xs={12}>
                            <Alert severity={"info"}>{t('page.organGestor.action.actualitzar.tabs.empty')}</Alert>
                        </Grid>
                    </> :<>
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
                                    <Node backgroundColor="#d6e9c6" color="black" minWidth="40% !important">
                                        <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${unitat?.codi} - ${unitat.oldDenominacio}`}</Typography>
                                    </Node>
                                    <Grid item sx={{display: "flex", alignItems: "center", justifyContent: "center" }}>
                                        <Divider orientation="horizontal" flexItem sx={{ borderColor: "black", borderWidth: 2, width: 40 }}/>
                                    </Grid>
                                    <Node backgroundColor="#faebcc" color="black" minWidth="40% !important">
                                        <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${unitat?.codi} - ${unitat.denominacioCooficial}`}</Typography>
                                    </Node>
                                </Grid>
                            </>)}
                        </CardData>
                        <CardData title={t('page.organGestor.action.actualitzar.tabs.new')} rowSpacing={2} hidden={prediccio?.unitatsNew?.length == 0}>
                            {prediccio?.unitatsNew?.map?.((unitat:any) => <NodeGrup nodeKey={unitat}/>)}
                        </CardData>
                        <CardData title={t('page.organGestor.action.actualitzar.tabs.del')} rowSpacing={2} hidden={prediccio?.unitatsExtingides?.length == 0}>
                            {prediccio?.unitatsExtingides?.map?.((unitat:any) =>
                                <Grid container item xs={12} direction="row" wrap="nowrap" justifyContent={"space-around"} alignItems="center" columnSpacing={1} rowSpacing={1}>
                                    <Node backgroundColor="#f8d7da" color="black" minWidth="40% !important">
                                        <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${unitat?.codi} - ${unitat.denominacioCooficial}`}</Typography>
                                    </Node>
                                </Grid>
                            )}
                        </CardData>
                    </>}
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