import React from 'react';

export const useDebounce = (value: any, delay: number = 500) => {
    const [debouncedValue, setDebouncedValue] = React.useState<any>();
    React.useEffect(() => {
        const timeoutId = setTimeout(() => setDebouncedValue(value), delay);
        return () => clearTimeout(timeoutId);
    }, [value, delay]);
    return debouncedValue;
}