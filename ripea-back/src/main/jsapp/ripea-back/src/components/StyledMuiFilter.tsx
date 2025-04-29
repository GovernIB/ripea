import {Button, Grid, Icon} from "@mui/material";
import {MuiFilter, useFilterApiRef, useFormApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useSession} from "./SessionStorageContext.tsx";
import {useEffect} from "react";

const filterStyle = { sx: {mb: 2, p: 2, backgroundColor: '#f5f5f5', border: '1px solid #e3e3e3', borderRadius: '4px'} };

export type FilterButtonProps = {
    value: string;
    text: string;
    icon?: string;
    componentProps?: any;
};

const StyledMuiFilter = (props:any) => {
    const { t } = useTranslation();
    const filterRef = useFilterApiRef();
    const formRef = useFormApiRef();

    const defaultButtons = [
        {
            value: 'clear',
            text: t('common.clear'),
            componentProps: {
                variant: "outlined",
                sx: {borderRadius: '4px'}
            }
        },
        {
            value: 'search',
            text: t('common.search'),
            icon: 'filter_alt',
            componentProps: {
                variant: "contained",
                sx: {borderRadius: '4px'}
            }
        }
    ];

    const cercar = ()=> {
        apiRef.current.filter()
        saveFilterData(formApiRef.current.getData())
    }
    const netejar = ()=> {
        saveFilterData(null)
        apiRef.current.clear()
    }

    const callback = (value:string) :void => {
        if (value=='clear') {
            netejar()
        }
        if (value=='search') {
            cercar()
        }
    }

    const { buttons = defaultButtons, buttonCallback = callback, apiRef = filterRef, formApiRef = formRef, springFilterBuilder, onSpringFilterChange, commonFieldComponentProps, componentProps, children, code, ...other } = props

    const { value: filterData, save: saveFilterData } = useSession(code);

    useEffect(() => {
        if (!!filterData) {
            onSpringFilterChange(
                springFilterBuilder(filterData)
            )
        }
    }, []);

    return <MuiFilter
        code={code}
        apiRef={apiRef}
        formApiRef={formApiRef}
        commonFieldComponentProps={{size: 'small', ...commonFieldComponentProps}}
        componentProps={{...filterStyle, ...componentProps}}
        buttonControlled

        initialData={filterData}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        onDataChange={(data:any) => {
            if (data && Object.keys(data).length > 0 && !filterData) {
                cercar()
            }
        }}
        {...other}
    >
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {children}

            <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'end' }}>
                {
                    buttons?.map((button:FilterButtonProps)=>
                        <Button key={button.value} onClick={() => buttonCallback?.(button.value)} {...button?.componentProps}>
                            {button?.icon && <Icon>{button?.icon}</Icon>}
                            {button.text}
                        </Button>)
                }
            </Grid>
        </Grid>
    </MuiFilter>
}
export default StyledMuiFilter;