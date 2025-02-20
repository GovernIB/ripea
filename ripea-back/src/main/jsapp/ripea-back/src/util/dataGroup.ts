import dayjs from 'dayjs';

export const generateDataGroups = (dataInici: string, dataFi: string, agrupacio: string) => {
    const dataGroups: string[] = [];
    let djs = dayjs(dataInici);
    const dataFiJs = dayjs(dataFi);
    do {
        dataGroups.push(djs.format('YYYY-MM-DDTHH:mm:ss'));
        if (agrupacio === 'ANY') {
            djs = djs.add(1, 'year');
        } else if (agrupacio === 'MES') {
            djs = djs.add(1, 'month');
        } else if (agrupacio === 'DIA') {
            djs = djs.add(1, 'day');
        } else if (agrupacio === 'HORA') {
            djs = djs.add(1, 'hour');
        } else if (agrupacio === 'MINUT') {
            djs = djs.add(1, 'minute');
        }
    } while (!djs.isAfter(dataFiJs));
    return dataGroups;
}

export const isDataInGroup = (data: string, group: string, agrupacio: string) => {
    if (agrupacio === 'ANY') {
        return data.substring(0, 4) === group.substring(0, 4);
    } else if (agrupacio === 'MES') {
        return data.substring(0, 7) === group.substring(0, 7);
    } else if (agrupacio === 'DIA') {
        return data.substring(0, 10) === group.substring(0, 10);
    } else if (agrupacio === 'HORA') {
        return data.substring(0, 13) === group.substring(0, 13);
    } else if (agrupacio === 'MINUT') {
        return data.substring(0, 16) === group.substring(0, 16);
    }
}

export const toXAxisDataGroups = (dataGroups: string[], agrupacio: string) => {
    return dataGroups?.map(g => {
        if (agrupacio === 'ANY') {
            return g.substring(0, 4);
        } else if (agrupacio === 'MES') {
            return g.substring(0, 7);
        } else if (agrupacio === 'DIA') {
            return g.substring(0, 10);
        } else if (agrupacio === 'HORA') {
            return g.substring(11, 13) + ':00';
        } else if (agrupacio === 'MINUT') {
            return g.substring(11, 16);
        }
    });
}