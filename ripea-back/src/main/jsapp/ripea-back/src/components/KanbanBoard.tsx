import {useState, useCallback, useMemo, useEffect} from 'react';
import {
    DndContext,
    DragOverlay,
    KeyboardSensor,
    MouseSensor,
    TouchSensor,
    useDraggable,
    useDroppable,
    useSensor,
    useSensors,
    pointerWithin
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
    onElementClick?: (element:any) => void,
    actions?: any[]
}

const KanbanCard = ({ icon, task, onClick, onContextMenu, isDragging }: any) => {
    const canDrag = task.draggable !== false;
    const { attributes, listeners, setNodeRef, isDragging: isDraggingLocal } = useDraggable({
        id: task.id,
        data: task,
        disabled: !canDrag
    });
    const dragging = isDragging || isDraggingLocal;
    const clickable = Boolean(onClick);
    // Les targetes que no es poden arrossegar no reben els atributs de dnd-kit (role i tabIndex),
    // així que si són clicables se'ls han de posar aquí perquè també s'hi pugui arribar amb el
    // teclat. Les arrossegables ja els porten, i allà l'espai i l'intro els fa servir el sensor
    // de teclat per agafar i deixar anar la targeta.
    const clickableProps = clickable && !canDrag ? {
        role: 'button',
        tabIndex: 0,
        onKeyDown: (e: any) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                onClick(e, task);
            }
        },
    } : {};
    return (
        <Card
            ref={setNodeRef}
            onContextMenu={(e:any) => onContextMenu?.(e, task)}
            // Amb el botó esquerre la targeta fa dues coses: arrossegar-la per canviar-ne l'estat
            // o, si no hi ha hagut arrossegament, obrir-la. No es trepitgen perquè els sensors del
            // tauler no activen el drag fins que el punter s'ha desplaçat uns quants píxels i,
            // quan s'activa, dnd-kit atura el click que ve després de deixar anar.
            onClick={clickable ? (e:any) => onClick(e, task) : undefined}
            {...(canDrag ? { ...listeners, ...attributes } : {})}
            {...clickableProps}
            sx={{
                cursor: canDrag ? (dragging ? 'grabbing' : 'grab') : clickable ? 'pointer' : 'default',
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

    return (
        <Grid size={12}>
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
                    transition: 'all 0.2s',
                }}
            >
                <Typography variant="subtitle2" sx={{ mb: 1.5, textAlign: 'center', color: 'text.secondary' }}>
                    {title} ({tasks.length})
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>{children}</Box>
            </Box>
        </Grid>
    );
};

const KanbanColumn = ({ icon, title, subcolumns, onCreate, onClick, onContextMenu, activeId }: any) => {
    const { t } = useTranslation();
    return (
        <Box
            sx={{
                p: 2,
                // La columna ha de quedar un escaló per sota dels blocs d'estat (que van amb
                // `background.paper`, igual que la card principal). Als temes foscos (obscur i
                // dràcula) `background.paper` és el mateix color que la card, i per això s'hi
                // fa servir `background.default`; al tema clar `background.default` és blanc i
                // cal `grey.100`.
                bgcolor: (theme) => (theme.palette.mode === 'dark' ? 'background.default' : 'grey.100'),
                borderRadius: 3,
                height: '100%',
            }}
        >
            <Typography variant="h6" sx={{ mb: 2, px: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                {title}
                {onCreate && (
                    <IconButton title={t('common.create')} onClick={onCreate}>
                        <Icon>add_circle</Icon>
                    </IconButton>
                )}
            </Typography>
            <Grid container spacing={1} sx={{ display: 'flex', alignItems: 'flex-start' }}>
                {subcolumns.map((sub: any) => (
                    <KanbanSubcolumn key={sub.id} id={sub.id} title={sub.title} tasks={sub.tasks}>
                        {sub.tasks.map((task: any) => (
                            <KanbanCard
                                key={task.id}
                                icon={sub.icon || icon}
                                task={task}
                                onClick={onClick}
                                onContextMenu={onContextMenu}
                                isDragging={task.id === activeId}
                            />
                        ))}
                    </KanbanSubcolumn>
                ))}
            </Grid>
        </Box>
    );
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

const KanbanBoard = ({ columns: columnDefs, elements, onCreate, handleDragEnd: onExternalDragEnd, onElementClick, actions }: KanbanBoardProp) => {
    const [activeId, setActiveId] = useState<string | null>(null);

    // Per defecte dnd-kit engega l'arrossegament tan bon punt es prem el botó, i això deixa la
    // targeta sense clic. Amb aquests sensors el drag demana un gest explícit (moure el ratolí uns
    // quants píxels o mantenir premut en tàctil), de manera que un clic o un toc curt segueixen
    // sent un clic. El sensor de tàctil amb retard també deixa desplaçar la pàgina amb el dit.
    const sensors = useSensors(
        useSensor(MouseSensor, { activationConstraint: { distance: 5 } }),
        useSensor(TouchSensor, { activationConstraint: { delay: 250, tolerance: 5 } }),
        useSensor(KeyboardSensor),
    );

    const [optimisticElements, setOptimisticElements] = useState<KanbanElementsProp[] | null>(null);
    const currentElements = optimisticElements || elements;

    const columns: KanbanColumnsProp[] = useMemo(
        () => groupTasksWithSubcolumns(currentElements || [], columnDefs || []),
        [currentElements, columnDefs]
    );

    const getActiveTask = useCallback(() => {
        if (!activeId) return null;
        return currentElements?.find((t: any) => String(t.id) === String(activeId)) || null;
    }, [activeId, currentElements]);

    const handleDragStart = useCallback((event: any) => {
        setActiveId(event.active.id);
    }, []);

    const handleDragEnd = useCallback(
        (event: any) => {
            setActiveId(null);
            const { active, over } = event;
            if (!active || !over) return;

            const origen = active.data?.current?.estat;
            const desti = over.id;
            const task = active.data?.current;

            if (origen === desti) return;

            const updatedElements = (currentElements || []).map((t) => (String(t.id) === String(task.id) ? { ...t, estat: desti } : t));
            setOptimisticElements(updatedElements);
            onExternalDragEnd?.(origen, desti, task);
        },
        [currentElements, onExternalDragEnd]
    );

    useEffect(() => {
        setOptimisticElements(null);
    }, [elements]);

    const [selectedTask, setSelectedTask] = useState<any>(null);
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

    // Botó dret: menú d'accions de l'element.
    const eventContextMenu = useCallback((e: any, element: any) => {
        e.preventDefault();
        if (element) {
            setSelectedTask(element.entity);
            setAnchorEl(e.target as HTMLElement);
        }
    }, []);

    // Botó esquerre sense arrossegament: l'acció principal de l'element, que decideix el pare.
    const eventClick = useCallback((_e: any, element: any) => {
        if (element) {
            onElementClick?.(element.entity ?? element);
        }
    }, [onElementClick]);

    const handleMenuClose = () => {
        setAnchorEl(null);
        setSelectedTask(null);
    };

    return (
        <Box>
            <DndContext sensors={sensors} onDragStart={handleDragStart} onDragEnd={handleDragEnd} collisionDetection={pointerWithin}>
                <Grid container spacing={1}>
                    {columns.map((col, i) => (
                        <Grid size={{ xs: 12, md: 12 / columns.length }} key={col.id}>
                            <KanbanColumn
                                icon={col.icon}
                                title={col.title}
                                col={col}
                                subcolumns={col.subcolumns}
                                onCreate={i == 0 ? onCreate : undefined}
                                onClick={onElementClick ? eventClick : undefined}
                                onContextMenu={eventContextMenu}
                                activeId={activeId}
                            />
                        </Grid>
                    ))}
                </Grid>

                <DragOverlay dropAnimation={{ duration: 200, easing: 'cubic-bezier(0.18, 0.67, 0.6, 1.22)' }}>
                    {activeId ? <KanbanCard task={getActiveTask()} isDragging /> : null}
                </DragOverlay>
            </DndContext>

            {actions && (
                <Menu
                    anchorEl={anchorEl}
                    open={Boolean(anchorEl)}
                    onClose={handleMenuClose}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                    transformOrigin={{ vertical: 'top', horizontal: 'right' }}
                >
                    {selectedTask && actionToItem(selectedTask, actions)}
                </Menu>
            )}
        </Box>
    );
};
export default KanbanBoard
