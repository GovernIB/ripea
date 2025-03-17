import Moment from "moment/moment";

export const formatDate = (date: string, format: string = 'DD/MM/Y HH:mm:ss'): string | null => {
    return date ? Moment(date).format(format) : null;
}

export const formatIso = (date: string) =>{
    return formatDate(date, "Y-MM-DDTHH:mm:ss")
}