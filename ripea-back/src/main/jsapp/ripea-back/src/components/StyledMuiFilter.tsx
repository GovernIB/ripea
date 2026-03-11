import {Button, Grid, Icon, Typography} from "@mui/material";
import {MuiFilter, useFilterApiRef, useFormApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useSession} from "./SessionStorageContext.tsx";
import {useEffect, useMemo} from "react";
import {GridButtonField} from "./GridFormField.tsx";

const filterStyle = { className: "styledFilter" };

export type FilterButtonProps = {
    value: string;
    text?: string;
    icon?: string;
    componentProps?: any;
};

// type StyledMuiFormProps = MuiFilterProps & {
//     buttons?: FilterButtonProps[],
//     buttonCallback?: (code:any) => void,
//     sessionKey?: string,
//     advancedSearch?: boolean;
// }

const StyledMuiFilter = (props:any) => {
    const { t } = useTranslation();
    const filterRef = useFilterApiRef();
    const formRef = useFormApiRef();

    const defaultButtons = useMemo<FilterButtonProps[]>(() => [
        {
            value: 'clear',
            text: t('common.clear'),
            icon: 'auto_fix_normal',
            componentProps: {
                variant: "outlined",
                sx: { borderRadius: '4px' },
            },
        },
        {
            value: 'search',
            text: t('common.filter'),
            icon: 'filter_alt',
            componentProps: {
                variant: "contained",
                sx: { borderRadius: '4px' },
            },
        },
    ], [filterRef]);

    const callback = (value: string) => {
        if (value === 'clear') netejar();
        if (value === 'search') cercar();
    };

    const {
        buttons = defaultButtons,
        buttonCallback = callback,
        buttonGridProps,
        apiRef = filterRef,
        formApiRef = formRef,
        springFilterBuilder,
        onSpringFilterChange,
        commonFieldComponentProps,
        componentProps,
        children,
        code,
        sessionKey = code,
        advancedSearch = false,
        ...other
    } = props

    const cercar = ()=> {
        apiRef?.current?.filter?.()
        if (sessionKey) {
            saveFilterData(formApiRef?.current?.getData?.())
        }
    }
    const netejar = ()=> {
        if (sessionKey) {
            saveFilterData(null)
        }
        apiRef?.current?.clear?.()
    }

    const { value: filterData, save: saveFilterData } = useSession(sessionKey);

    useEffect(() => {
        if (!!sessionKey && filterData && onSpringFilterChange && springFilterBuilder) {
            onSpringFilterChange(springFilterBuilder(filterData));
        }
    }, []);

    return <MuiFilter
        code={code}
        apiRef={apiRef}
        formApiRef={formApiRef}
        commonFieldComponentProps={{size: 'small', ...commonFieldComponentProps}}
        componentProps={{...filterStyle, ...componentProps}}
        buttonControlled
        filterOnFieldEnterKeyPressed

        initialData={filterData}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        onDataChange={(data:any) => {
            if (data && Object.keys(data).length > 0 && (!!sessionKey && !filterData)) {
                cercar()
            }
        }}
        {...other}
    >
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {children}

            <Grid item xs={2.4} sx={{ display: 'flex', justifyContent: 'end', marginLeft: 'auto' }} {...buttonGridProps}>
                {advancedSearch && <GridButtonField name={"advanced"} icon={"filter_list"}/>}
                {
                    buttons?.map((button:FilterButtonProps)=>
                        <Button key={button.value} onClick={() => buttonCallback?.(button.value)} {...button?.componentProps}>
                            {button?.icon && <Icon sx={{ mr: 0 }}>{button?.icon}</Icon>}
                            <Typography sx={{ paddingLeft: '5px', marginTop: '1px' }}>{button.text}</Typography>
                        </Button>)
                }
            </Grid>
        </Grid>
    </MuiFilter>
}
export default StyledMuiFilter;