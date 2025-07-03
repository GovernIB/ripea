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