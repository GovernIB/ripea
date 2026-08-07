import FullCalendar from "@fullcalendar/react";
import themePlugin from "@fullcalendar/react/themes/monarch";
import weekGridPlugin from '@fullcalendar/react/timegrid';
import multiMonthPlugin from '@fullcalendar/react/multimonth';
import '@fullcalendar/react/skeleton.css';
import '@fullcalendar/react/themes/monarch/theme.css';
import '@fullcalendar/react/themes/monarch/palettes/purple.css';

import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useCallback, useEffect, useRef, useState} from "react";
import {actionToItem} from "@src/components/MenuButton.tsx";
import {Menu, useTheme} from "@mui/material";
import {useTranslation} from "react-i18next";
import {useSession} from "@src/components/SessionStorageContext.tsx";

export const TascaCalendar = (props:any) => {
    const {actions, filter, namedQueries, perspectives, reloadTrigger, onEventClick, ...other} = props;
    const { t } = useTranslation();
    const theme = useTheme();
    const [tasques, setTasques] = useState<any[]>();
    const {value: view, save: setView} = useSession('calendarView');

    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService('expedientTascaResource');
    const {temporalMessageShow, currentLanguage} = useBaseAppContext();

    const refresh = () => {
        if (apiIsReady) {
            apiFind({filter, namedQueries, perspectives, unpaged: true})
                .then((result) => {
                    setTasques(result.rows)
                })
                .catch((error) => {
                    setTasques([])
                    temporalMessageShow(null, error?.message, 'error');
                })
        }
    }
    useEffect(() => {
        refresh()
    }, [filter, apiIsReady, reloadTrigger]);

    const renderEvent = (tasca:any) :any => ({
        id: tasca.id,
        title: tasca.titol ?? tasca.metaExpedientTasca.description,
        start: tasca.dataInici,
        end: tasca.dataLimit,
        color: tasca?.dataLimitExpirada
            ?'#ef5350'
            :tasca?.shouldNotifyAboutDeadline
                ?'#ffb74d'
                :'#81c784',
    })

    const [anchorPosition, setAnchorPosition] = useState<{top: number, left: number} | null>(null);
    const [selectedTask, setSelectedTask] = useState<any>(null);

    // Botó esquerre: l'acció principal de la tasca, la mateixa que el clic damunt la fila a la
    // vista de taula. La decideix el component pare perquè no és igual a totes les pantalles.
    const eventClick = useCallback((eventInfo: any) => {
        const tasca = tasques?.find((t) => t.id == eventInfo.event.id);
        if (tasca) {
            onEventClick?.(tasca);
        }
    }, [tasques, onEventClick]);

    // Botó dret: menú d'accions, l'equivalent del menú de la fila a la vista de taula.
    // FullCalendar no té cap callback per al botó dret, així que el listener s'afegeix a
    // l'element de cada esdeveniment quan es munta. Com que només s'hi registra un cop, el que
    // ha de fer es llegeix d'una referència: si es capturés directament treballaria sempre amb
    // la llista de tasques del primer render.
    const contextMenuShowRef = useRef<(id: string, el: HTMLElement, jsEvent: MouseEvent) => void>(() => {});
    contextMenuShowRef.current = (id, el, jsEvent) => {
        const tasca = tasques?.find((t) => t.id == id);
        if (tasca) {
            // Quan s'obre amb el teclat (Maj+F10 o la tecla de menú contextual) l'esdeveniment
            // no porta coordenades: llavors el menú s'ancora a l'element de la tasca.
            const rect = el.getBoundingClientRect();
            setSelectedTask(tasca);
            setAnchorPosition({
                top: jsEvent.clientY || rect.bottom,
                left: jsEvent.clientX || rect.left,
            });
        }
    };
    const eventDidMount = useCallback((info: any) => {
        info.el.addEventListener('contextmenu', (jsEvent: MouseEvent) => {
            jsEvent.preventDefault();
            contextMenuShowRef.current(info.event.id, info.el, jsEvent);
        });
    }, []);

    const handleMenuClose = () => {
        setAnchorPosition(null);
        setSelectedTask(null);
    };

    return <>
        <FullCalendar
            colorScheme={theme.palette.mode}
            locale={currentLanguage}
            plugins={[themePlugin, weekGridPlugin, multiMonthPlugin]}
            initialView={view ?? "dayGridMonth"}
            events={tasques?.map(t => renderEvent(t)) ?? []}
            eventClick={eventClick}
            eventDidMount={actions ? eventDidMount : undefined}
            firstDay={1}
            todayText={t('calendar.today')}
            weekTextLong={t('calendar.week')}
            monthText={t('calendar.month')}
            yearText={t('calendar.year')}
            datesSet={(info) => setView(info.view.type)}
            {...other}
            headerToolbar={{
                // start: 'prevYear,prev,next,nextYear',
                start: 'prev,next,today',
                center: 'title',
                end : `timeGridWeek,dayGridMonth,multiMonthYear`,
                ...other.headerToolbar
            }}
        />

        {actions && <Menu
            anchorReference="anchorPosition"
            anchorPosition={anchorPosition ?? undefined}
            open={Boolean(anchorPosition)}
            onClose={handleMenuClose}
        >
            {selectedTask && actionToItem(selectedTask, actions)}
        </Menu>}
    </>
}
