import {useMemo, useState} from "react";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Avisos, StyledEstat} from "../../expedient/ExpedientGrid.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {formatDate} from "../../../util/dateUtils.ts";
import {Link} from "@mui/material";
import {Link as RouterLink} from "react-router-dom";

const sortModel:any = [{ field: 'createdDate', sort: 'desc' }];
const perspectives = ["ESTAT"];

const ExpedientDialog = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    const columns = useMemo(() => [
        {
            field: 'numero',
            flex: 1,
            renderCell: (params: any) => <Link component={RouterLink} to={`/contingut/${params?.id}`}>{params.formattedValue}</Link>
        },
        {
            field: 'nom',
            flex: 1.6,
        },
        {
            headerName: t('page.expedient.detall.avisos'),
            field: 'avisos',
            sortable: false,
            flex: 0.5,
            renderCell: (params: any) => <Avisos entity={params?.row}/>,
        },
        {
            field: 'estat',
            flex: 0.5,
            renderCell: (params: any) => <StyledEstat entity={params?.row} icon={"folder"}>{params.formattedValue}</StyledEstat>,
            sortProcessor: (field: string, sort: GridSortDirection) => {
                return [
                    { field: "estatAdditional", sort },
                    { field: field, sort },
                    { field: "id", sort }
                ]
            }
        },
        {
            field: 'createdDate',
            flex: 0.6,
            valueFormatter: (value: any) => formatDate(value),
        },
        {
            field: 'agafatPer',
            flex: 0.75,
            // hidden: !user?.conf?.expedientListAgafatPer,
            // valueFormatter: (value: any) => value?.description,
        },
    ], [t]);

    return <>
        <StyledMuiGrid
            resourceName={'expedientResource'}
            columns={columns}
            filter={builder.eq('metaExpedient.id', entity?.id)}
            sortModel={sortModel}
            perspectives={perspectives}
            toolbarHide

            rowProps={(row: any) => {
                const color = row?.estatAdditionalInfo?.color;
                return color
                    ? {
                        'box-shadow': `${color} -6px 0px 0px`,
                        'border-left': `6px solid ${color}`,
                    }
                    : {
                        'padding-left': '6px'
                    }
            }}

            style={{ minHeight: 110 + 52 * 10 }}
            readOnly
        />
    </>
}

const useExpedientDialog = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (_id:any, row:any) => {
        setEntity(row)
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
        },
    ];

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.metaExpedient.action.expedient.title', {nom: entity?.nom})}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={buttons}
            buttonCallback={() :void => {
                handleClose()
            }}
        >
            <Load value={entity}>
                <ExpedientDialog entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useExpedientDialog;