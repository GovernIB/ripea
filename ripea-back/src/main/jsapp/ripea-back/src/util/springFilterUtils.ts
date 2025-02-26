
const filter = (options :any[]) :any[] => {
    return options.filter(a=>a!=null && a.length>0 && !a.includes("null") && !a.includes("undefined"));
}

export const and = (...options :any[]) :string => {
    return filter(options).join(" AND ");
}
export const or = (...options :any[]) :string => {
    return filter(options).join(" OR ");
}
export const like = (option :string, value :string) :string => {
    return `${option}~'%${value}%'`;
}
export const neq = (option :string, value :any) :string => {
    return `${option}!${value}`;
}
export const eq = (option :string, value :any) :string => {
    return `${option}:${value}`;
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