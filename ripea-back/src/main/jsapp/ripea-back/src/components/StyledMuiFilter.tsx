import {Grid, Typography} from "@mui/material";
import {MuiFilter, useFilterApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useSession} from "./SessionStorageContext.tsx";
import {useEffect, useMemo, useRef} from "react";
import {CombinedIcon, GridButton, GridButtonField} from "./GridFormField.tsx";

const filterStyle = { className: "styledFilter" };

// Width (px) below which the advanced-filter action buttons collapse to
// icon-only, so their text never gets clipped in the narrow button container.
// Shared by the filters that use advancedSearch (Expedient, Tasques, Anotacions)
// to keep their collapse point in sync.
export const FILTER_ADVANCED_ICON_ONLY_BREAKPOINT = 1615;

// Clau amb què el filtre desa les seves dades a la sessió. Ha d'incloure el resourceName:
// el `code` sol ("FILTER") és genèric i el comparteixen filtres sense relació (OrganGestor,
// Integracio, Contingut...), provocant que les dades persistides d'un filtre es filtrin a un
// altre (p. ex. estat='V' d'òrgans arribant al monitor d'integracions).
export const filterSessionKey = (resourceName?: string, code?: string) =>
    [resourceName, code].filter(Boolean).join('.');

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
        springFilterBuilder,
        onSpringFilterChange,
        onDataChange: externalOnDataChange,
        commonFieldComponentProps,
        componentProps,
        children,
        code,
        // Dades amb què s'inicialitza el filtre mentre l'usuari no n'hagi desat cap a la
        // sessió (p. ex. el revisor arriba al llistat de procediments amb l'estat de revisió
        // a PENDENT). Es desen a la sessió amb la cerca automàtica inicial, i a partir
        // d'aquí mana sempre el que hi hagi desat.
        defaultData,
        sessionKey = code != null ? filterSessionKey(props.resourceName, code) : undefined,
        advancedSearch = false,
        buttonIconOnlyBreakpoint = 'lg',
        ...other
    } = props

    // Marca que la propera notificació de dades prové d'un "netejar", per re-aplicar
    // el filtre buit només quan el formulari no té camps per defecte (vegeu onDataChange).
    const netejarPendingRef = useRef(false);

    const cercar = ()=> {
        apiRef?.current?.filter?.()
    }
    const netejar = ()=> {
        if (sessionKey) {
            saveFilterData(null)
        }
        netejarPendingRef.current = true;
        apiRef?.current?.clear?.()
    }

    const callback = (value: string) => {
        if (value === 'clear') netejar();
        if (value === 'search') cercar();
        buttonCallback?.(value)
    };

    const { value: filterData, save: saveFilterData } = useSession(sessionKey);

    // El guardat es fa aquí (i no a cercar()) perquè onSpringFilterChange és l'únic
    // punt pel qual passen totes les aplicacions reals del filtre: el botó "Filtrar",
    // la tecla Intro (filterOnFieldEnterKeyPressed, que dispara filter() dins de lib
    // sense passar per cercar()) i l'auto-cerca inicial. Així Intro i botó persisteixen
    // igual. clear() amb buttonControlled no crida filter(), per tant no re-desa.
    const handleSpringFilterChange = (springFilter:any) => {
        if (sessionKey) {
            saveFilterData(apiRef?.current?.getData?.())
        }
        onSpringFilterChange?.(springFilter);
    };

    useEffect(() => {
        if (!!sessionKey && filterData && onSpringFilterChange && springFilterBuilder) {
            onSpringFilterChange(springFilterBuilder(filterData));
        }
    }, []);

    const buttonSize = 12 / (buttons.length + (advancedSearch ?1 :0))

    return <MuiFilter
        code={code}
        apiRef={apiRef}
        commonFieldComponentProps={{size: 'small', ...commonFieldComponentProps}}
        componentProps={{...filterStyle, ...componentProps}}
        buttonControlled
        filterOnFieldEnterKeyPressed

        initialData={filterData ?? defaultData}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={handleSpringFilterChange}
        onDataChange={(data:any) => {
            const hasData = data && Object.keys(data).length > 0;
            if (hasData && (!!sessionKey && !filterData)) {
                // Auto-cerca inicial (i "netejar" amb camps per defecte, que deixen dades).
                cercar()
            } else if (netejarPendingRef.current && !hasData) {
                // "Netejar" sense camps per defecte: el revert deixa les dades buides,
                // així que el branch d'auto-cerca no s'activa i cal re-aplicar el filtre
                // buit aquí. Amb camps per defecte no s'hi entra → sense doble cerca.
                cercar()
            }
            netejarPendingRef.current = false;
            externalOnDataChange?.(data);
        }}
        {...other}
    >
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {children}

            <Grid container direction={"row"} columnSpacing={1} rowSpacing={1} size={{xs: 12, sm: 6, md: 2.4}} sx={{ display: 'flex', justifyContent: 'end', marginLeft: 'auto' }} {...buttonGridProps}>
                {advancedSearch && <GridButtonField
                    size={buttonSize}
                    name={"advanced"}
                    title={(active:boolean) => active
                        ?t('common.advancedSearchClose')
                        :t('common.advancedSearchOpen')}
                    icon={(active:boolean) => active
                        ?<CombinedIcon base={"zoom_out"} badge={"expand_less"}/>
                        :<CombinedIcon base={"zoom_in"} badge={"expand_more"}/>}
                />}
                {
                    buttons?.map((button:FilterButtonProps)=>
                        <GridButton size={buttonSize}
                                    key={button.value}
                                    title={button.text}
                                    icon={button.icon}
                                    iconOnlyBreakpoint={buttonIconOnlyBreakpoint}
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