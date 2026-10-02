import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {Box, Icon, Tooltip, Typography} from "@mui/material";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import {useUsuariActions} from "./UsuariActions.tsx";

/** Columna "Actiu": per als usuaris donats de baixa, el tooltip mostra qui, quan i per què. */
const ActiuCell = ({row}: { row: any }) => {
    const {t} = useTranslation();
    if (row?.actiu) {
        return <Icon role="img" aria-label={t('page.usuari.actiu')}>check</Icon>
    }
    const info = t('page.usuari.baixa.info', {
        data: row?.baixaData ? formatDate(row.baixaData) : '',
        usuari: row?.baixaUsuari ?? '',
        motiu: row?.baixaMotiu ?? '',
    });
    return <Tooltip title={<Typography variant="body2" sx={{whiteSpace: 'pre-line'}}>{info}</Typography>}>
        <Box component="span" sx={{display: 'inline-flex'}} tabIndex={0} aria-label={info}>
            <Icon color="error">person_off</Icon>
        </Box>
    </Tooltip>
}

const RolCell = ({value}: { value?: string }) => {
    const {t} = useTranslation();
    return value ? <>{t(`enum.rol.${value}`, {defaultValue: value})}</> : null;
}

const columns = [
    {field: 'codi', flex: 0.6},
    {field: 'nom', flex: 1.2},
    {field: 'nif', flex: 0.5},
    {field: 'email', flex: 1},
    {field: 'idioma', flex: 0.4},
    {field: 'entitatPerDefecte', flex: 0.8},
    {field: 'entitatActual', flex: 0.8},
    {
        field: 'rolActual',
        flex: 0.7,
        renderCell: (params: any) => <RolCell value={params?.row?.rolActual}/>,
    },
    {
        field: 'actiu',
        flex: 0.3,
        renderCell: (params: any) => <ActiuCell row={params?.row}/>,
    },
]
const sortModel: any[] = [{field: 'codi', sort: 'asc'}];

export const UsuariGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components, handlePermisos} = useUsuariActions(refresh);

    return <GridPage autoHeight>
        <CardPage title={t('navigate.usuari')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"usuariResource"}
                columns={columns}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                onRowDoubleClick={(params: any) => handlePermisos(params?.row?.id, params?.row)}
                toolbarShowQuickFilter
                onRefresh={refresh}
                readOnly
            />
            {components}
        </CardPage>
    </GridPage>
}
export default UsuariGrid;
