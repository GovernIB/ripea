import React from "react";
import {useDraggable, useDroppable} from "@dnd-kit/core";
import {GridRow} from "@mui/x-data-grid-pro";
import {Icon, IconButton} from "@mui/material";
import {useTranslation} from "react-i18next";

type DraggableContextType = {
    draggableAttributes: any;
    draggableListeners: any;
    draggableSetActivatorNodeRef: (element: HTMLElement | null) => void;
}
const DraggableContext = React.createContext<DraggableContextType | undefined>(undefined);
export const useDraggableContext = () => {
    const context = React.useContext(DraggableContext);
    if (context === undefined) {
        throw new Error('useDraggableContext must be used within an DraggableContext.Provider');
    }
    return context;
}

interface DraggableContainerProps {
    id: string | number;
    data?: any;
    style?: any;
    styleOver?: any;
    children: React.ReactNode;
}

export const DraggableItem: React.FC<DraggableContainerProps> = ({ id, data, style, styleOver, children }) => {
    const {
        attributes: draggableAttributes,
        listeners: draggableListeners,
        transform: draggableTransform,
        setNodeRef: draggableSetNodeRef,
        setActivatorNodeRef: draggableSetActivatorNodeRef,
    } = useDraggable({ id: `draggable_${id}`, data });

    const { isOver, setNodeRef: droppableSetNodeRef } = useDroppable({ id: `droppable_${id}`, data });

    // Combinar refs de draggable + droppable en un solo div
    const combinedRef = (node: HTMLDivElement | null) => {
        draggableSetNodeRef(node);
        droppableSetNodeRef(node);
    };

    const draggableStyle = draggableTransform
        ? { transform: `translate3d(${draggableTransform.x}px, ${draggableTransform.y}px, 0)`, zIndex: 1, position: 'relative' }
        : undefined;

    const droppableStyle = {
        borderTop: isOver ? '2px solid grey' : undefined,
        ...(isOver ?styleOver :{}),
        ...(style ?? {}),
    };

    return (
        <div ref={combinedRef} style={droppableStyle}>
            <DraggableContext.Provider
                value={{ draggableAttributes, draggableListeners, draggableSetActivatorNodeRef }}
            >
                <div style={draggableStyle}>
                    {children}
                </div>
            </DraggableContext.Provider>
        </div>
    );
};

export const DraggableGridRow: React.FC<any> = (props) => {
    const droppableStyle = {
        border: props.row.tipus === 'CARPETA' ? '2px solid grey' : undefined,
        borderTop: props.row.tipus !== 'CARPETA' ? '2px solid grey' : undefined,
    };
    return (
        <DraggableItem id={props.row.id} data={props.row} styleOver={droppableStyle}>
            <GridRow {...props} />
        </DraggableItem>
    );
}
export const DraggableGridRowHandler: React.FC = () => {
    const { t } = useTranslation();
    const { draggableAttributes, draggableListeners, draggableSetActivatorNodeRef } = useDraggableContext();
    return <IconButton
        size="small"
        title={t('common.dragdrop')}
        ref={draggableSetActivatorNodeRef}
        {...draggableAttributes}
        {...draggableListeners}
        sx={{ cursor: 'grab', mr: 1 }}>
        <Icon sx={{ mr: 0 }}>swap_vert</Icon>
    </IconButton>;
}