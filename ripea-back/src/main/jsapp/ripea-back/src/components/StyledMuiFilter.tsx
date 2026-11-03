import {Grid, Typography} from "@mui/material";
import {MuiFilter, useFilterApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useSession} from "./SessionStorageContext.tsx";
import {useEffect, useMemo, useRef} from "react";
import {GridButton, GridButtonField} from "./GridFormField.tsx";
import FilterAutoApply from "./FilterAutoApply.tsx";
import {FilterAutoApplyContext, normalizeFilterData} from "./FilterAutoApplyContext.ts";
import {icons} from "../util/icons.ts";
import {outlinedControlBorderSx} from "./titleBarControlsSx.ts";

const filterStyle = { className: "styledFilter" };

/**
 * Columnes (de 12) que ocupa el bloc de botons del filtre, per punt de ruptura. És l'única font de
 * les mides del bloc: es pot ajustar tot des d'aquí.
 *
 * El bloc simple només duu el botó "Neteja" (amb icona): amb `md: 0.5` a ~900px d'amplada són uns
 * 17px i la icona hi queda justa (cal que el botó no tingui padding). Amb la cerca avançada (`advancedSearch`)
 * n'hi ha dos: el commutador (fletxa de desplegar/contreure + text "Avançat"/"Bàsic") i "Neteja" (només icona).
 *
 * Els camps de cada filtre han de sumar `12 - FILTER_BUTTONS_COLUMNS.md` a `md` (o
 * `12 - FILTER_BUTTONS_COLUMNS_ADVANCED.md` amb cerca avançada) a l'última fila perquè el bloc càpiga
 * a la mateixa fila.
 */
export const FILTER_BUTTONS_COLUMNS = { xs: 12, sm: 2, md: 0.5 } as const;
export const FILTER_BUTTONS_COLUMNS_ADVANCED = { xs: 12, sm: 3, md: 1.5 } as const;

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
    /** Només es mostra la icona (el text queda com a títol/tooltip). */
    iconOnly?: boolean;
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
            icon: icons.neteja,
            iconOnly: true,
        },
    ], [t]);

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
        // Aplica el filtre sol en canviar-ne els camps (veure FilterAutoApply). Només s'ha de desactivar als
        // filtres que s'executen amb un botó propi perquè la cerca té efectes (p. ex. el d'InteressatsGridForm).
        autoApply = true,
        ...other
    } = props

    // Marca que la propera notificació de dades prové d'un "netejar", per re-aplicar
    // el filtre buit només quan el formulari no té camps per defecte (vegeu onDataChange).
    const netejarPendingRef = useRef(false);
    // Dades (normalitzades) de l'últim filtre aplicat i cancel·lació de l'aplicació automàtica pendent
    // (veure FilterAutoApply, que els fa servir per no repetir cerques).
    const lastAppliedRef = useRef<string | undefined>(undefined);
    const autoApplyCancelRef = useRef<(() => void) | undefined>(undefined);

    const cercar = ()=> {
        apiRef?.current?.filter?.()
    }
    const netejar = ()=> {
        if (sessionKey) {
            saveFilterData(null)
        }
        netejarPendingRef.current = true;
        autoApplyCancelRef.current?.();
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
        lastAppliedRef.current = normalizeFilterData(apiRef?.current?.getData?.());
        if (sessionKey) {
            saveFilterData(apiRef?.current?.getData?.())
        }
        onSpringFilterChange?.(springFilter);
    };

    useEffect(() => {
        if (!!sessionKey && filterData && onSpringFilterChange && springFilterBuilder) {
            lastAppliedRef.current = normalizeFilterData(filterData);
            onSpringFilterChange(springFilterBuilder(filterData));
        }
    }, []);

    // Amb cerca avançada el commutador duu icona (fletxa) + text i ocupa la major part del bloc;
    // la resta de botons (només icona) es reparteixen el que queda.
    const advancedButtonSize = buttons.length > 0 ? 8 : 12;
    const otherButtonSize = advancedSearch ? 4 / Math.max(buttons.length, 1) : 12 / buttons.length;

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
        <FilterAutoApplyContext.Provider value={autoApply}>
            <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                {children}
                {autoApply && <FilterAutoApply lastAppliedRef={lastAppliedRef} cancelRef={autoApplyCancelRef} />}

                <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}
                      size={advancedSearch ? FILTER_BUTTONS_COLUMNS_ADVANCED : FILTER_BUTTONS_COLUMNS}
                      sx={{ display: 'flex', justifyContent: 'end', marginLeft: 'auto' }} {...buttonGridProps}>
                    {advancedSearch && <GridButtonField
                        size={advancedButtonSize}
                        name={"advanced"}
                        title={(active:boolean) => active
                            ?t('common.advancedSearchClose')
                            :t('common.advancedSearchOpen')}
                        text={(active:boolean) => active
                            ?t('common.advancedSearchClose')
                            :t('common.advancedSearchOpen')}
                        icon={(active:boolean) => active ?icons.plega :icons.desplega}
                        iconOnlyFrom="sm"
                        iconOnlyBreakpoint={buttonIconOnlyBreakpoint}
                        sx={{ whiteSpace: 'nowrap', overflow: 'hidden' }}
                    />}
                    {
                        buttons?.map((button:FilterButtonProps)=>
                            <GridButton size={otherButtonSize}
                                        key={button.value}
                                        title={button.text}
                                        icon={button.icon}
                                        iconOnlyBreakpoint={buttonIconOnlyBreakpoint}
                                        sx={[button.iconOnly ? {minWidth: 0, px: 0} : {}, outlinedControlBorderSx]}
                                        onClick={() => callback(button.value)}
                                        {...button?.componentProps}>
                                {!button.iconOnly && <Typography sx={{paddingLeft: '5px', marginTop: '1px', maxWidth: 'max-content'}}>{button.text}</Typography>}
                            </GridButton>)
                    }
                </Grid>
            </Grid>
        </FilterAutoApplyContext.Provider>
    </MuiFilter>
}
export default StyledMuiFilter;