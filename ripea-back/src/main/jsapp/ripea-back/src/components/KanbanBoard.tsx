import {useState, useCallback, useMemo, useEffect} from 'react';
import {
    DndContext,
    DragOverlay,
    useDraggable,
    useDroppable,
    closestCorners
} from '@dnd-kit/core';
import {Card, CardContent, Typography, Box, Chip, Grid, Menu, Icon, IconButton} from '@mui/material';
import {actionToItem} from "@src/components/MenuButton.tsx";
import {TextAvatar} from "reactlib";
import {useTranslation} from "react-i18next";

type KanbanColumnsProp = {
    id: string,
    title: string,
    [key: string]: any;
}
type KanbanElementsProp = {
    id: number,
    title: string,
    estat: string,
    tags?: string[],
    [key: string]: any;
}
type KanbanBoardProp = {
    columns: KanbanColumnsProp[],
    elements: KanbanElementsProp[],
    onCreate?: (estat:string) => void,
    handleDragEnd: (origen:string, desti:string, element:any) => void,
    actions?: any[]
}

const KanbanCard = ({ icon, task, onClick, isDragging }: any) => {
    const canDrag = task.draggable !== false;
    const { attributes, listeners, setNodeRef, isDragging: isDraggingLocal } = useDraggable({
        id: task.id,
        data: task,
        disabled: !canDrag
    });
    const dragging = isDragging || isDraggingLocal;
    return (
        <Card
            ref={setNodeRef}
            onContextMenu={(e:any) => onClick?.(e, task)}
            // onClick={(e:any) => onClick?.(e, task)}
            {...(canDrag ? { ...listeners, ...attributes } : {})}
            sx={{
                cursor: canDrag ? (dragging ? 'grabbing' : 'grab') : 'default',
                opacity: dragging ? 0.4 : canDrag ? 1 : 0.6,
                transition: 'opacity 0.2s, transform 0.2s',
                '&:active': canDrag ? { cursor: 'grabbing' } : {},
                boxShadow: dragging ? 'none' : 1,
                position: 'relative'
            }}
        >
            <CardContent>
                <Box display={'flex'} alignItems={'center'} justifyContent={'space-between'}>
                    <Typography display={'flex'} alignItems={'center'} variant="body1" fontWeight="medium">{icon && <Icon>{icon}</Icon>}{task.title}</Typography>
                    {task?.user && <TextAvatar text={task?.user}/>}
                </Box>
                {task.tags?.length > 0 && (
                    <Box sx={{ mt: 1, display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                        {task.tags?.map?.((tag: any) => (
                            tag.label && <Chip key={tag.label} {...tag} size="small" sx={{height: 20}}/>
                        ))}
                    </Box>
                )}
            </CardContent>
        </Card>
    );
};

const KanbanSubcolumn = ({ id, title, tasks, children }: any) => {
    const { setNodeRef, isOver } = useDroppable({ id });

    return (<Grid size={12}>
        <Box
            ref={setNodeRef}
            sx={{
                flex: 1,
                minWidth: 0,
                p: 1.5,
                bgcolor: isOver ? 'action.hover' : 'background.paper',
                borderRadius: 2,
                border: isOver ? '2px dashed' : '2px solid transparent',
                borderColor: isOver ? 'primary.main' : 'transparent',
                transition: 'all 0.2s'
            }}
        >
            <Typography variant="subtitle2" sx={{ mb: 1.5, textAlign: 'center', color: 'text.secondary' }}>
                {title} ({tasks.length})
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                {children}
            </Box>
        </Box>
    </Grid>);
};

const KanbanColumn = ({ icon, title, subcolumns, onCreate, onClick, activeId }: any) => {
    const { t } = useTranslation();
    return <Box sx={{ p: 2, bgcolor: 'grey.100', borderRadius: 3, height: '100%' }}>
        <Typography variant="h6" sx={{ mb: 2, px: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            {title}{onCreate && <IconButton title={t('common.create')} onClick={onCreate}><Icon>add_circle</Icon></IconButton>}</Typography>
        <Grid container spacing={1} sx={{ display: 'flex', alignItems: 'flex-start' }}>
            {subcolumns.map((sub:any) => (
                <KanbanSubcolumn key={sub.id} id={sub.id} title={sub.title} tasks={sub.tasks}>
                    {sub.tasks.map((task:any) => (
                        <KanbanCard key={task.id} icon={sub.icon || icon} task={task} onClick={onClick} isDragging={task.id === activeId} />
                    ))}
                </KanbanSubcolumn>
            ))}
        </Grid>
    </Box>
};

const groupTasksWithSubcolumns = (
    tasks: any[],
    columnDefs: any[],
) => {
    const subMap = new Map<string, any>();
    const columns = columnDefs.map(def => ({
        ...def,
        subcolumns: def.subcolumns.map((sub:any) => {
            const resolved = { ...sub, tasks: [] as any[] };
            subMap.set(sub.id, resolved);
            return resolved;
        })
    }));

    tasks.forEach(task => {
        const key = task.estat;
        const target = subMap.get(key);
        if (target) {
            target.tasks.push(task);
        }
    });

    return columns;
};

const KanbanBoard = ({
                         columns: columnDefs,
                         elements,
                         onCreate,
                         handleDragEnd: onExternalDragEnd,
                         actions
                     }: KanbanBoardProp) => {
    const [activeId, setActiveId] = useState<string | null>(null);

    const [optimisticElements, setOptimisticElements] = useState<KanbanElementsProp[] | null>(null);
    const currentElements = optimisticElements || elements;

    const columns: KanbanColumnsProp[] = useMemo(() =>
            groupTasksWithSubcolumns(currentElements || [], columnDefs || []),
        [currentElements, columnDefs]);

    const getActiveTask = useCallback(() => {
        if (!activeId) return null;
        return currentElements?.find((t: any) => String(t.id) === String(activeId)) || null;
    }, [activeId, currentElements]);

    const handleDragStart = useCallback((event: any) => {
        setActiveId(event.active.id);
    }, []);

    const handleDragEnd = useCallback((event: any) => {
        setActiveId(null);
        const { active, over } = event;
        if (!active || !over) return;

        const origen = active.data?.current?.estat;
        const desti = over.id;
        const task = active.data?.current;

        if (origen === desti) return;

        const updatedElements = (currentElements || []).map(t =>
            String(t.id) === String(task.id) ? { ...t, estat: desti } : t
        );
        setOptimisticElements(updatedElements);
        onExternalDragEnd?.(origen, desti, task);
    }, [currentElements, onExternalDragEnd]);

    useEffect(() => {
        setOptimisticElements(null);
    }, [elements]);

    const [selectedTask, setSelectedTask] = useState<any>(null);
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

    const eventClick = useCallback((e: any, element:any) => {
        e.preventDefault();
        if (element) {
            setSelectedTask(element.entity);
            setAnchorEl(e.target as HTMLElement);
        }
    }, []);

    const handleMenuClose = () => {
        setAnchorEl(null);
        setSelectedTask(null);
    };

    return (
        <Box>
            <DndContext
                onDragStart={handleDragStart}
                onDragEnd={handleDragEnd}
                collisionDetection={closestCorners}
            >
                <Grid container spacing={1}>
                    {columns.map((col, i) => (
                        <Grid size={{ xs: 12, md: 12 / columns.length }} key={col.id}>
                            <KanbanColumn icon={col.icon} title={col.title} col={col} subcolumns={col.subcolumns} onCreate={i == 0 ?onCreate :undefined} onClick={eventClick} activeId={activeId} />
                        </Grid>
                    ))}
                </Grid>

                <DragOverlay dropAnimation={{ duration: 200, easing: 'cubic-bezier(0.18, 0.67, 0.6, 1.22)' }}>
                    {activeId ? <KanbanCard task={getActiveTask()} isDragging /> : null}
                </DragOverlay>
            </DndContext>

            {actions && <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
                transformOrigin={{vertical: 'top', horizontal: 'right'}}
            >
                {selectedTask && actionToItem(selectedTask, actions)}
            </Menu>}
        </Box>
    );
};
export default KanbanBoard
