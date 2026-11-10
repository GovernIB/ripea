const isoDurationRegex =
    /(-)?P(?:([.,\d]+)Y)?(?:([.,\d]+)M)?(?:([.,\d]+)W)?(?:([.,\d]+)D)?T(?:([.,\d]+)H)?(?:([.,\d]+)M)?(?:([.,\d]+)S)?/;

export const durationParseIso = (isoDuration: string) => {
    const matches = isoDuration?.match(isoDurationRegex);
    if (matches != null) {
        const duration = {
            sign: matches?.[1] === undefined ? '+' : '-',
            years: matches?.[2] === undefined ? 0 : parseInt(matches[2]),
            months: matches?.[3] === undefined ? 0 : parseInt(matches[3]),
            weeks: matches?.[4] === undefined ? 0 : parseInt(matches[4]),
            days: matches?.[5] === undefined ? 0 : parseInt(matches[5]),
            hours: matches?.[6] === undefined ? 0 : parseInt(matches[6]),
            minutes: matches?.[7] === undefined ? 0 : parseInt(matches[7]),
            seconds: matches?.[8] === undefined ? 0 : parseInt(matches[8]),
        };
        processDuration(duration);
        return duration;
    } else {
        return null;
    }
};

export const durationFormat = (isoDuration: string, noSeconds?: boolean) => {
    const duration = durationParseIso(isoDuration);
    if (duration != null) {
        const parts: string[] = [];
        duration.years > 0 && parts.push(duration.years + 'y');
        duration.months > 0 && parts.push(duration.months + 'm');
        duration.days > 0 && parts.push(duration.days + 'd');
        duration.hours > 0 && parts.push(duration.hours + 'h');
        parts.push(duration.minutes + 'm');
        !noSeconds && parts.push(duration.seconds + 's');
        return parts.join(' ');
    } else {
        return duration;
    }
};

export const durationAddSeconds = (duration: string, amount: number = 1): string => {
    const match = duration.match(
        /^(-)?P(?:(\d+(?:\.\d+)?)D)?(?:T(?:(\d+(?:\.\d+)?)H)?(?:(\d+(?:\.\d+)?)M)?(?:(\d+(?:\.\d+)?)S)?)?$/
    );
    if (!match) {
        throw new Error(`Invalid ISO 8601 duration: ${duration}`);
    }
    const sign = match[1] ? -1 : 1;
    const days = Number(match[2] ?? 0);
    const hours = Number(match[3] ?? 0);
    const minutes = Number(match[4] ?? 0);
    const seconds = Number(match[5] ?? 0);
    const totalSeconds = sign * (days * 86400 + hours * 3600 + minutes * 60 + seconds) + amount;
    const resultSign = totalSeconds < 0 ? '-' : '';
    let remaining = Math.abs(totalSeconds);
    const resultDays = Math.floor(remaining / 86400);
    remaining %= 86400;
    const resultHours = Math.floor(remaining / 3600);
    remaining %= 3600;
    const resultMinutes = Math.floor(remaining / 60);
    const resultSeconds = remaining % 60;
    let result = `${resultSign}P`;
    if (resultDays) result += `${resultDays}D`;
    if (resultHours || resultMinutes || resultSeconds || !resultDays) {
        result += 'T';
        if (resultHours) result += `${resultHours}H`;
        if (resultMinutes) result += `${resultMinutes}M`;
        if (resultSeconds || (!resultHours && !resultMinutes)) result += `${resultSeconds}S`;
    }
    return result;
};

const processDuration = (duration: any) => {
    processDurationField(duration, 'years', 'months', 12);
    processDurationField(duration, 'days', 'hours', 24);
    processDurationField(duration, 'hours', 'minutes', 60);
    processDurationField(duration, 'minutes', 'seconds', 60);
};

const processDurationField = (duration: any, bigField: string, smallField: string, num: number) => {
    const div = Math.floor(duration[smallField] / num);
    const remainder = duration[smallField] % num;
    if (div > 0) {
        duration[bigField] = duration[bigField] + div;
        duration[smallField] = remainder;
    }
};
