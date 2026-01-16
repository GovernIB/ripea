import {ContenidoData} from "./CardData.tsx";
import React, { ReactNode } from "react";
import {Grid} from "@mui/material";

type FieldDataProps = {
    field: string;
    entity?: any;
    getField?: (name: string) => any;
};

export const FieldData = ({ field, entity, getField, ...other }: FieldDataProps) => {
    const f = getField?.(field) ?? {};
    const value = f?.options?.[entity?.[field]] ?? entity?.[field];

    return <ContenidoData title={f?.label ?? field} {...other}>{value}</ContenidoData>;
};

type MuiDetailProps = {
    entity: any;
    fields: any[];
    children: ReactNode;
};

export const MuiDetail = ({ entity, fields, children }: MuiDetailProps) => {
    const getField = (name: string) => fields.find((item: any) => item?.name === name) ?? {};

    return (<Grid container sx={{ display:'flex', flexDirection: "row", wordWrap: "break-word" }} columnSpacing={1} rowSpacing={1}>
        {/* Clonamos los hijos para inyectar props comunes */}
        {React.Children.map(children, (child: any) =>
            React.isValidElement(child)
                ? React.cloneElement(child, { entity, getField })
                : child
        )}
    </Grid>);
};