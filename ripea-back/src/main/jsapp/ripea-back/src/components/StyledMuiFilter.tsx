import {Grid2 as Grid, Typography} from "@mui/material";
import {MuiFilter, useFilterApiRef, useFormApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useSession} from "./SessionStorageContext.tsx";
import {useEffect, useMemo} from "react";
import {GridButton, GridButtonField} from "./GridFormField.tsx";

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
        },
        {
            value: 'search',
            text: t('common.filter'),
            icon: 'filter_alt',
            componentProps: {
                variant: "contained",
            },
        },
    ], [filterRef]);

    const {
        buttons = defaultButtons,
        buttonCallback,
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

    const callback = (value: string) => {
        if (value === 'clear') netejar();
        if (value === 'search') cercar();
        buttonCallback?.(value)
    };

    const { value: filterData, save: saveFilterData } = useSession(sessionKey);

    useEffect(() => {
        if (!!sessionKey && filterData && onSpringFilterChange && springFilterBuilder) {
            onSpringFilterChange(springFilterBuilder(filterData));
        }
    }, []);

    const buttonSize = 12 / (buttons.length + (advancedSearch ?1 :0))

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

            <Grid container direction={"row"} columnSpacing={1} rowSpacing={1} size={{xs: 12, sm: 6, md: 2.4}} sx={{ display: 'flex', justifyContent: 'end', marginLeft: 'auto' }} {...buttonGridProps}>
                {advancedSearch && <GridButtonField size={buttonSize} name={"advanced"} title={t('common.advancedSearch')} icon={"filter_list"}/>}
                {
                    buttons?.map((button:FilterButtonProps)=>
                        <GridButton size={buttonSize}
                                    key={button.value}
                                    title={button.text}
                                    icon={button.icon}
                                    onClick={() => callback(button.value)}
                                    {...button?.componentProps}>
                            <Typography sx={{paddingLeft: '5px', marginTop: '1px', maxWidth: 'max-content'}}>{button.text}</Typography>
                        </GridButton>)
                }
            </Grid>
        </Grid>
    </MuiFilter>
}
export default StyledMuiFilter;