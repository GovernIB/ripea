import React from 'react';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridFooterContainer,
    useGridApiContext,
    useGridSelector,
    gridPageSelector,
    gridPageSizeSelector,
    selectedGridRowsCountSelector,
    GridPaginationModel,
    GridRowSelectionModel,
} from '@mui/x-data-grid-pro';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Pagination from '@mui/material/Pagination';
import { useBaseAppContext } from '../../BaseAppContext';

type DataGridFooterSelectionProps = {
    setRowSelectionModel: (rowSelectionModel: GridRowSelectionModel) => void;
};

type DataGridFooterPaginationProps = {
    paginationModel: GridPaginationModel;
    pageInfo: any;
    pageSizeOptions: any;
};

type DataGridFooterProps = {
    selectionActive: boolean;
    paginationActive: boolean;
    paginationModel: GridPaginationModel;
    pageInfo: any;
    pageSizeOptions: number[];
    setRowSelectionModel: (rowSelectionModel: GridRowSelectionModel) => void;
};

const DataGridFooterSelection: React.FC<DataGridFooterSelectionProps> = (props) => {
    const { setRowSelectionModel } = props;
    const { t } = useBaseAppContext();
    const apiRef = useGridApiContext();
    const selectedRowCount = useGridSelector(apiRef, selectedGridRowsCountSelector);
    const handleClearClick = () => {
        setRowSelectionModel([]);
    }
    const selectedMessage = selectedRowCount > 1 ? t('grid.selection.multiple', { count: selectedRowCount }) : t('grid.selection.one');
    return <Box sx={{ ml: 2 }}>
            {selectedRowCount ? selectedMessage : null}
        {selectedRowCount ? <IconButton onClick={handleClearClick} size="small">
                    <Icon fontSize="small">clear</Icon>
        </IconButton> : null}
    </Box>;
}

const GridFooterPagination: React.FC<DataGridFooterPaginationProps> = (props) => {
    const { paginationModel, pageInfo, pageSizeOptions } = props;
    const { t } = useBaseAppContext();
    const apiRef = useGridApiContext();
    const page = useGridSelector(apiRef, gridPageSelector);
    const pageSize = useGridSelector(apiRef, gridPageSizeSelector);
    if (pageInfo?.totalElements) {
        const pageCount =
            pageInfo?.totalElements && pageSize
                ? Math.ceil(pageInfo.totalElements / pageSize)
                : undefined;
        const pageRowCount =
            pageInfo?.totalElements <= pageSize
                ? pageInfo?.totalElements
                : page === (pageCount ?? 0) - 1
                  ? pageInfo?.totalElements % pageSize || pageSize
                  : pageSize;
        const firstElementIndex = page * pageSize + 1;
        const lastElement = Math.min(firstElementIndex + pageRowCount - 1, pageInfo?.totalElements);
        const boxStyle = { display: 'flex', justifContent: 'flex-end', alignItems: 'center' };
        const currentPageSize = paginationModel?.pageSize;
        return (
            <Box style={boxStyle}>
                {pageSizeOptions && (
                    <Box sx={{ mr: 4 }}>
                        <FormControl size="small">
                            <Select
                                value={currentPageSize}
                                onChange={(event) =>
                                    apiRef.current.setPaginationModel({
                                        page,
                                        pageSize: event.target.value as number,
                                    })
                                }>
                                {pageSizeOptions.map((o: number) => (
                                    <MenuItem value={o}>{o}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                )}
                <Box>
                    {pageInfo != null
                        ? t('grid.pageInfo', {
                              from: firstElementIndex,
                              to: lastElement,
                              count: pageInfo.totalElements,
                          })
                        : ''}
                </Box>
                <Pagination
                    color="primary"
                    count={pageSize ? pageCount : 0}
                    page={page + 1}
                    onChange={(_event, value) => apiRef.current.setPage(value - 1)}
                />
            </Box>
        );
    } else {
        return (
            <Pagination
                color="primary"
                count={0}
                onChange={(_event, value) => apiRef.current.setPage(value - 1)}
            />
        );
    }
};

const DataGridFooter: React.FC<DataGridFooterProps> = (props) => {
    const {
        selectionActive,
        paginationActive,
        paginationModel,
        pageInfo,
        pageSizeOptions,
        setRowSelectionModel,
    } = props;
    const showFooter = selectionActive || paginationActive;
    return showFooter ? (
        <GridFooterContainer>
            <DataGridFooterSelection setRowSelectionModel={setRowSelectionModel} />
            {paginationActive && (
                <GridFooterPagination
                    paginationModel={paginationModel}
                    pageInfo={pageInfo}
                    pageSizeOptions={pageSizeOptions}
                />
            )}
        </GridFooterContainer>
    ) : null;
};

export default DataGridFooter;
