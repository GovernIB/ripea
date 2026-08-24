import { useState, useEffect } from 'react';
import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import KanbanBoard from "@src/components/KanbanBoard.tsx";
import {formatDate} from "@src/util/dateUtils.ts";
import {useUserSession} from "@src/components/Session.tsx";

export const TascaKanban = ({actions, filter, namedQueries, perspectives, reloadTrigger, onEventClick}:any) => {
    const { t } = useTranslation();
    const [tasques, setTasques] = useState<any[]>([]);
    const { value: user } = useUserSession();

    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
        find: apiFind,
        currentFields: fields
    } = useResourceApiService('expedientTascaResource');
    const {temporalMessageShow} = useBaseAppContext();
    const prioritat = fields?.find((f: { name: string; label: string }) => f.name === "prioritat")

    const isOnlyObservador = (row: any): boolean => {
        return row?.observadors?.map((obs:any)=>obs?.id).includes(user.codi) && !row?.usuariActualResponsable && !row?.usuariActualDelegat;
    }

    const toKanbanElement = (t:any) => {
        return {
            id: t.id,
            title: t.titol ?? t.metaExpedientTasca.description,
            estat: t.estat,
            tags: [
                {
                    label: formatDate(t?.dataLimit, "DD/MM/Y"),
                    title: 'dataLimit',
                    color: t.dataLimitExpirada ?'error'
                        :t.shouldNotifyAboutDeadline ?'warning'
                        :'success'
                },
                {
                    label: prioritat?.options?.[t.prioritat],
                    title: 'prioritat',
                    color: t.prioritat == "A_BAIXA" ? "success"
                        :t.prioritat == "B_NORMAL" ? "default"
                        :t.prioritat == "C_ALTA" ? "warning"
                        :t.prioritat == "D_MOLT_ALTA" ? "error"
                        :undefined
                },
                {
                    label: t.expedient?.description,
                    title: 'expedient',
                },
            ],
            user: t?.responsableActual?.description.replace(` (${t?.responsableActual?.id})`,''),
            draggable: !isOnlyObservador(t) && !(t.estat == 'REBUTJADA' || t.estat == 'CANCELLADA' || t.estat == 'FINALITZADA'),
            entity: t
        }
    }

    const refresh = () => {
        if (apiIsReady) {
            apiFind({filter, namedQueries, perspectives, unpaged: true})
                .then((result) => {
                    setTasques(result.rows.map(t=> toKanbanElement(t)))
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

    const handleDragEnd = (_origen:any, desti:any, tasca:any) => {
        apiAction(tasca.id,{code:'CHANGE_ESTAT', data:{estat: desti, motiu: t('page.tasca.kanban.changeEstat.motiu')}})
            .catch((error) => {
                refresh?.()
                temporalMessageShow(null, error.message, 'error');
            });
    };

    return (
        <KanbanBoard
            elements={tasques}
            columns={[
                { id: 'todo', title: t('page.tasca.kanban.todo'), icon: 'circle', subcolumns: [
                    { id: 'PENDENT', title: t('enum.tascaEstat.PENDENT'), icon: 'schedule' },
                    { id: 'AGAFADA', title: t('enum.tascaEstat.AGAFADA') }
                ]},
                { id: 'progress', title: t('page.tasca.kanban.progress'), icon: 'adjust', subcolumns: [
                    { id: 'INICIADA', title: t('enum.tascaEstat.INICIADA') }
                ]},
                { id: 'done', title: t('page.tasca.kanban.done'), icon: 'do_not_disturb_on', subcolumns: [
                    { id: 'REBUTJADA', title: t('enum.tascaEstat.REBUTJADA') },
                    { id: 'CANCELLADA', title: t('enum.tascaEstat.CANCELLADA') },
                    { id: 'FINALITZADA', title: t('enum.tascaEstat.FINALITZADA'), icon: 'check_circle' }
                ]}
            ]}
            actions={actions}
            handleDragEnd={handleDragEnd}
            onElementClick={onEventClick}
        />
    );
};
