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

export const DraggableGridRow: React.FC<any> = (props) => {
    const {
        attributes: draggableAttributes,
        listeners: draggableListeners,
        transform: draggableTransform,
        setNodeRef: draggableSetNodeRef,
        setActivatorNodeRef: draggableSetActivatorNodeRef
    } = useDraggable({
        id: 'draggable_' + props.row.id,
        data: props.row,
    });
    const droppableProps = useDroppable({
        id: 'droppable_' + props.row.id,
        data: props.row,
    });
    const { isOver, setNodeRef: droppableSetNodeRef } = droppableProps;
    const draggableStyle = draggableTransform ? {
        transform: `translate3d(${draggableTransform.x}px, ${draggableTransform.y}px, 0)`,
    } : undefined;
    const droppableStyle = {
        border: isOver && props.row.tipus === 'CARPETA' ? '2px solid grey' : undefined,
        borderTop: isOver && props.row.tipus !== 'CARPETA' ? '2px solid grey' : undefined,
    };
    return <div ref={droppableSetNodeRef} style={droppableStyle}>
        <DraggableContext.Provider
            value={{
                draggableAttributes,
                draggableListeners,
                draggableSetActivatorNodeRef
            }}>
            <GridRow
                ref={draggableSetNodeRef}
                style={draggableStyle}
                {...props}>
            </GridRow>
        </DraggableContext.Provider>
    </div>;
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