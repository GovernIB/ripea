import Moment from "moment/moment";

export const formatDate = (date : string, format : string = 'DD/MM/YY HH:mm:ss') :string => {
    return Moment(date).format(format)
}