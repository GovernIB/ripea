import {useTranslation} from "react-i18next";
import {MuiDialog, useFormContext} from "reactlib";
import {useMemo, useState} from "react";
import {Grid, Icon} from "@mui/material";
import Load from "../../../components/Load.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {configTypeValueLabel, getFieldFromItem} from "./PropietatsProps.tsx";

const PropietatsForm = ({item, itemField}:any) => {
    const {data, fields, apiRef} = useFormContext()

    const valueField = useMemo(() => ({
        ...itemField,
        label: fields?.filter(i=>i.name=='value')[0]?.label,
        key: data?.key,
    }), [itemField, data?.key]);

    if (data.id && valueField.type == 'checkbox' && typeof data.value == 'string') {
        apiRef.current?.setFieldValue('value', data.value == 'true')
    }
    if (!data?.id && !data?.description) {
        apiRef.current?.setFieldValue('key', item?.key)
        apiRef.current?.setFieldValue('description', item?.description)
        apiRef.current?.setFieldValue('value', itemField?.value)
    }

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name={'key'} disabled required/>
        <GridFormField name={'description'} required/>
        <GridFormField name={'value'} field={valueField} required/>
        <GridFormField name={'entitat'} required disabled={data?.id}/>
        <GridFormField name={'organ'} filter={builder.and(builder.eq('entitat.id', data?.entitat?.id))} disabled={data?.id}/>
        <GridFormField name={'configurableOrgansDescendents'}/>
    </Grid>
}

const perspectives:any[] = [];
const sortModel:any[] = [{field: 'entitatCodi', sort: 'asc'}];
// Constant de mòdul: amb un literal en línia cada render crea un objecte nou, i l'useEffect
// de MuiDataGrid el torna a aplicar retornant la graella a la primera pàgina.
const paginationModel = {page: 0, pageSize: 5};
const filter = builder.or(
    builder.neq('entitatCodi', null),
    builder.neq('organCodi ', null),
)

export const usePropietatsDialog = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [item, setItem] = useState<any>();
    const [itemField, setItemField] = useState<any>();

    // El valor es mostra amb l'etiqueta traduida del tipus de propietat, igual que al
    // formulari d'edicio. El valor desat no canvia: si no hi ha traduccio es mostra tal qual.
    const columns:any[] = useMemo(() => [
        {
            field: 'key',
            flex: 1,
        },
        {
            field: 'value',
            flex: 1,
            valueFormatter: (value: any) =>
                value != null && item?.type?.id != null
                    ? configTypeValueLabel(t, item.type.id, value)
                    : value,
        },
        {
            field: 'entitatCodi',
            flex: 0.25,
        },
        {
            field: 'organCodi',
            flex: 0.25,
        },
        {
            field: 'configurableOrgansDescendents',
            flex: 0.5,
            renderCell: (params:any) => params?.row.configurableOrgansDescendents && <Icon>check</Icon>
        },
    ], [item, t]);
    // console.log(item, itemField)

    const handleOpen = (_id:any, row:any) => {
        // console.log("row", row)
        setItem(row)
        setItemField(getFieldFromItem(row, t))
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setItem(undefined)
            setItemField(undefined)
            setOpen(false);
        }
    };

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
            componentProps: { variant: 'outlined' }
        },
    ]

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={`Configuraciónes específicas para: ${item?.description}`}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={buttons}
            buttonCallback={() :void => {
                handleClose();
            }}
        >
            <Load value={item}>
                <StyledMuiGrid
                    resourceName={'configResource'}
                    columns={columns}
                    filter={filter}
                    popupEditCreateActive
                    popupEditFormDialogResourceTitle={t('page.propietats.title')}
                    toolbarCreateTitle={t('page.propietats.action.new.label')}
                    popupEditFormContent={<PropietatsForm item={item} itemField={itemField}/>}
                    formAdditionalData={{
                        group: item?.group,
                        type: item?.type,
                    }}
                    rowAdditionalActions={actions}
                    sortModel={sortModel}
                    perspectives={perspectives}
                    namedQueries={[`QUERY_ESPECIFIQUES#${item?.key}`]}
                    autoHeight
                    paginationModel={paginationModel}
                    popupEditFormI18nKeys={{
                        createSuccess: 'page.propietats.action.new.ok',
                        updateSuccess: 'page.propietats.action.update.ok',
                        deleteSuccess: 'page.propietats.action.delete.ok',
                    }}
                />
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}