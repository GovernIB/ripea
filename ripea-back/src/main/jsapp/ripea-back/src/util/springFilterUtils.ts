
const filter = (options :any[]) :any[] => {
    return options.filter(a=>a!=null && a.length>0 && !a.includes("undefined") &&
        !(a.includes("null") && !(a.includes("is not null") || a.includes("is null"))));
}
export const and = (...options :any[]) :string => {
    const joinedValues = filter(options).join(' AND ')
    return joinedValues.includes(' AND ') ? `(${joinedValues})` : joinedValues;
}
export const or = (...options :any[]) :string => {
    const joinedValues = filter(options).join(' OR ')
    return joinedValues.includes(' OR ') ? `(${joinedValues})` : joinedValues;
}

export const like = (option :string, value :string) :string => {
    return `${option}~'%${value}%'`;
}
export const neq = (option :string, value :any) :string => {
    return value===null ?`${option} is not null` :`${option}!${value}`;
}
export const eq = (option :string, value :any) :string => {
    return value===null ?`${option} is null` :`${option}:${value}`;
}
export const equals = (option :string, value :any, equals :boolean) :string => {
    return equals ?eq(option,value) :neq(option,value);
}
export const concat = (...options :any[]) :string => {
    return options.length>0 ?`concat(${ filter(options).join(",' ',") })` :'';
}
export const exists = (value :string) :string => {
    return value.length>0 ?`exists(${value})` :'';
}

export const greaterThan = (option :string, value :any) :string => {
    return `${option}>${value}`;
}
export const greaterEq = (option :string, value :any) :string => {
    return `${option}>:${value}`;
}

export const lessThan = (option :string, value :any) :string => {
    return `${option}<${value}`;
}
export const lessEq = (option :string, value :any) :string => {
    return `${option}<:${value}`;
}
export const between = (option :string, paramStart :any, paramEnd :any) => {
    return and(
        greaterEq(option, paramStart),
        lessEq(option, paramEnd)
    );
}