import React, { KeyboardEvent } from 'react';
import Form from './Form';
import { FilterApi, FilterApiRef, FilterContext, useFilterContext } from './FilterContext';
import { FormApiRef, FormApi } from './FormContext';

/**
 * Propietats del component Filter.
 */
export type FilterProps = React.PropsWithChildren & {
    /** Nom del recurs de l'API REST d'on es consultarà la informació per a mostrar el filtre */
    resourceName: string;
    /** Codi de l'artefacte del filtre */
    code: string;
    /** Indica si l'aplicació del filtre està controlada per un botó o si s'aplica de forma automàtica */
    buttonControlled?: true;
    /** Referència a l'api del component */
    apiRef?: FilterApiRef;
    /** Referència a l'api del component Form */
    formApiRef?: FormApiRef;
    /** Funció encarregada de transforma les dades del filtre en una cadena en format Spring Filter */
    springFilterBuilder: (data: any) => string | undefined;
    /** Dades inicials pel filtre */
    initialData?: any;
    /** Dades addicionals pel filtre */
    additionalData?: any;
    /** Indica si s'ha de fer una petició onChange sense cap camp associat quan es crea el component */
    initOnChangeRequest?: true;
    /** Indica si s'ha de filtrar quan es pitgi la tecla Intro en algun camp */
    filterOnFieldEnterKeyPressed?: true;
    /** Propietats comunes per a tots els components FormField de dins aquest component */
    commonFieldComponentProps?: any;
    /** Event que es llença quan es modifica alguna dada del filtre */
    onDataChange?: (data: any) => void;
    /** Event que es llença quan hi ha canvis en el filtre Spring Filter que genera aquest component */
    onSpringFilterChange?: (springFilter: string | undefined) => void;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
};

/**
 * Hook per a accedir a l'API de Filter des de fora del context del component.
 *
 * @returns referència a l'API del component Filter.
 */
export const useFilterApiRef: () => React.RefObject<FilterApi> = () => {
    const filterApiRef = React.useRef<FilterApi | any>({});
    return filterApiRef;
};

/**
 * Hook per a accedir a l'API de Filter des de dins el context del component.
 *
 * @returns referència a l'API del component Filter.
 */
export const useFilterApiContext: () => FilterApiRef = () => {
    const filterContext = useFilterContext();
    return filterContext.apiRef;
};

/**
 * Component base de filtre independent de la llibreria de interfície d'usuari.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del filtre.
 */
export const Filter: React.FC<FilterProps> = (props) => {
    const {
        resourceName,
        code,
        buttonControlled,
        springFilterBuilder,
        initialData,
        additionalData,
        filterOnFieldEnterKeyPressed,
        onDataChange,
        onSpringFilterChange,
        apiRef: apiRefProp,
        formApiRef: formApiRefProp,
        children,
        ...otherFormProps
    } = props;
    const [nextDataChangeAsUncontrolled, setNextDataChangeAsUncontrolled] = React.useState(false);
    const apiRef = React.useRef<FilterApi>(undefined);
    const formApiRef = React.useRef<FormApi | any>({});
    if (formApiRefProp != null) {
        formApiRefProp.current = formApiRef.current;
    }
    const filter = (data?: any) => {
        formApiRef.current.validate().then(() => {
            const formData = data ?? formApiRef.current?.getData();
            const springFilter = springFilterBuilder(formData);
            onSpringFilterChange?.(springFilter);
        });
    };
    const clear = (data?: any) => {
        setNextDataChangeAsUncontrolled(true);
        formApiRef.current?.reset(data);
    };
    const handleDataChange = (data: any) => {
        onDataChange?.(data);
        if (nextDataChangeAsUncontrolled) {
            setNextDataChangeAsUncontrolled(false);
            filter(data);
        } else if (!buttonControlled) {
            filter(data);
        }
    };
    const fieldTypeMap = new Map<string, string>([
        ['datetime-local', 'date'],
        ['checkbox', 'checkbox-select'],
    ]);
    const handleFilterEnterKeyPressed = filterOnFieldEnterKeyPressed
        ? (e: KeyboardEvent<HTMLInputElement>) => {
              if (e.key === 'Enter') {
                  e.stopPropagation();
                  e.preventDefault();
                  filter();
              }
          }
        : undefined;
    apiRef.current = {
        clear,
        filter,
    };
    if (apiRefProp) {
        if (apiRefProp.current) {
            apiRefProp.current.clear = clear;
            apiRefProp.current.filter = filter;
        } else {
            console.warn('apiRef prop must be initialized with an empty object');
        }
    }
    const context = {
        resourceName,
        code,
        apiRef,
    };
    return (
        <FilterContext.Provider value={context}>
            <div onKeyDown={handleFilterEnterKeyPressed}>
                <Form
                    resourceName={resourceName}
                    resourceType="FILTER"
                    resourceTypeCode={code}
                    initialData={initialData}
                    additionalData={additionalData}
                    onDataChange={handleDataChange}
                    fieldTypeMap={fieldTypeMap}
                    apiRef={formApiRef}
                    {...otherFormProps}>
                    {children}
                </Form>
            </div>
        </FilterContext.Provider>
    );
};

export default Filter;
