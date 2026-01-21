import {ContenidoData} from "./CardData.tsx";
import React, { ReactNode } from "react";
import {Grid} from "@mui/material";
import Load from "./Load.tsx";

type FieldDataProps = {
    field: string;
    label?: string;
    children?: ReactNode;
};

export const FieldData = ({ label, field, children, ...other }: FieldDataProps) => {
    const { entity, getField } = useMuiDetailContext()
    const f = getField(field);
    const value = f?.options?.[entity?.[field]] ?? entity?.[field];

    return (
        <ContenidoData title={label ?? (f?.label ?? field)} {...other}>
            {children ?? (value?.description ?? value)}
        </ContenidoData>
    );
};

type MuiDetailContextType = { entity: any; getField: (name: string) => any; };
const MuiDetailContext = React.createContext<MuiDetailContextType | undefined>(undefined);

type MuiDetailProps = {
    entity: any;
    fields: any[];
    children: ReactNode;
};

const useMuiDetailContext = () => {
    const ctx = React.useContext(MuiDetailContext);
    if (!ctx) throw new Error('useMuiDetailContext debe usarse dentro de <MuiDetail>');
    return ctx;
}
export const MuiDetail = ({ entity, fields, children }: MuiDetailProps) => {
    const getField = (name: string) => fields?.find((item: any) => item?.name === name) ?? {};

    return (<MuiDetailContext.Provider value={{ entity, getField }}>
        <Load value={entity}>
            <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
                {children}
            </Grid>
        </Load>
    </MuiDetailContext.Provider>);
};