import { GridRowSelectionModel } from '@mui/x-data-grid-pro';

export const EMPTY_SELECTION_MODEL: GridRowSelectionModel = {
    type: 'include',
    ids: new Set(),
};

/** Converts an array of IDs (v7 format) or a v8 model to the v8 GridRowSelectionModel format. */
export const toSelectionModel = (ids: any[] | GridRowSelectionModel | undefined): GridRowSelectionModel => {
    if (ids == null) return EMPTY_SELECTION_MODEL;
    if (Array.isArray(ids)) return { type: 'include', ids: new Set(ids) };
    return ids;
};

/** Extracts an array of IDs from a v8 GridRowSelectionModel (or a legacy array). */
export const fromSelectionModel = (model: GridRowSelectionModel | any[] | undefined): any[] => {
    if (model == null) return [];
    if (Array.isArray(model)) return model;
    if (model.type === 'include') return Array.from(model.ids);
    return [];
};
