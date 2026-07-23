import FullCalendar from "@fullcalendar/react";
import themePlugin from "@fullcalendar/react/themes/monarch";
import weekGridPlugin from '@fullcalendar/react/timegrid';
import multiMonthPlugin from '@fullcalendar/react/multimonth';
import '@fullcalendar/react/skeleton.css';
import '@fullcalendar/react/themes/monarch/theme.css';
import '@fullcalendar/react/themes/monarch/palettes/purple.css';

import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useCallback, useEffect, useState} from "react";
import {actionToItem} from "@src/components/MenuButton.tsx";
import {Menu} from "@mui/material";
import {useTranslation} from "react-i18next";
import {useSession} from "@src/components/SessionStorageContext.tsx";

export const TascaCalendar = (props:any) => {
    const {actions, filter, namedQueries, perspectives, reloadTrigger, ...other} = props;
    const { t } = useTranslation();
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

    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    const [selectedTask, setSelectedTask] = useState<any>(null);
    const eventClick = useCallback((eventInfo: any) => {
        const task = tasques?.find((t) => t.id == eventInfo.event.id);
        if (task) {
            setSelectedTask(task);
            setAnchorEl(eventInfo.el);
        }
    }, [tasques]);

    const handleMenuClose = () => {
        setAnchorEl(null);
        setSelectedTask(null);
    };

    return <>
        <FullCalendar
            locale={currentLanguage}
            plugins={[themePlugin, weekGridPlugin, multiMonthPlugin]}
            initialView={view ?? "dayGridMonth"}
            events={tasques?.map(t => renderEvent(t)) ?? []}
            eventClick={eventClick}
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
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
            transformOrigin={{vertical: 'top', horizontal: 'right'}}
        >
            {selectedTask && actionToItem(selectedTask, actions)}
        </Menu>}
    </>
}
