import Moment from "moment/moment";

export const formatDate = (date: string, format: string = 'DD/MM/Y HH:mm:ss'): string | null => {
    return date ? Moment(date).format(format) : null;
}

export const formatIso = (date: string) =>{
    return formatDate(date, "Y-MM-DDTHH:mm:ss")
}

export const formatStartOfDay = (date: string) =>{
    return formatDate(date, "Y-MM-DDT00:00:00")
}

export const formatEndOfDay = (date: string) =>{
    return formatDate(date, "Y-MM-DDT23:59:59")
}

const formatDuracioEnDies = (dies: number | null | undefined, t: any): string | null => {
    if (dies == null) return null;
    if (dies <= 0) return t('page.tasca.detall.duracioFormat.mateixDia');
    const weeks = Math.floor(dies / 7);
    const remainingDays = dies % 7;
    let retorn = '';
    if (weeks > 0) {
        retorn += weeks === 1
            ? t('page.tasca.detall.duracioFormat.setmana_1')
            : t('page.tasca.detall.duracioFormat.setmana_n', { count: weeks });
    }
    if (remainingDays > 0) {
        const diaStr = remainingDays === 1
            ? t('page.tasca.detall.duracioFormat.dia_1')
            : t('page.tasca.detall.duracioFormat.dia_n', { count: remainingDays });
        retorn += retorn.length > 0 ? t('page.tasca.detall.duracioFormat.i') + diaStr : diaStr;
    }
    return retorn + '.';
}

const formatDiesRestants = (dataLimit: string | null | undefined, t: any): string | null => {
    if (!dataLimit) return null;
    const dFin = new Date(dataLimit);
    dFin.setHours(0, 0, 0, 0);
    const dAvui = new Date();
    dAvui.setHours(0, 0, 0, 0);
    if (dFin < dAvui) return t('page.tasca.detall.duracioFormat.expirada');
    if (dFin.getTime() === dAvui.getTime()) return t('page.tasca.detall.duracioFormat.avui');
    const diffMs = dFin.getTime() - dAvui.getTime();
    const days = Math.floor(diffMs / (1000 * 60 * 60 * 24)) + 1;
    return t('page.tasca.detall.duracioFormat.falten', { count: days });
}

export const formatDuracio = (
    duracio: number | null | undefined,
    dataLimit: string | null | undefined,
    t: any
): string | null => {
    const duracioStr = formatDuracioEnDies(duracio, t);
    const restantsStr = formatDiesRestants(dataLimit, t);
    if (duracioStr !== null) {
        if (!dataLimit) return duracioStr;
        return `${duracioStr} (${restantsStr})`;
    }
    return restantsStr;
}