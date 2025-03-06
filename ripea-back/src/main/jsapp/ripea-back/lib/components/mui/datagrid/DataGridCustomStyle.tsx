import { alpha, styled } from '@mui/material/styles';
import { DataGridPro as DataGrid, gridClasses } from '@mui/x-data-grid-pro';

const ODD_OPACITY = 0.2;

const DataGridCustomStyle = styled(DataGrid)(({ theme }) => ({
    [`& .${gridClasses.row}.even`]: {
        backgroundColor: theme.palette.grey[200],
        '&:hover': {
            backgroundColor: alpha(theme.palette.primary.main, ODD_OPACITY),
            '@media (hover: none)': {
                backgroundColor: 'transparent',
            },
        },
        '&.Mui-selected': {
            backgroundColor: alpha(
                theme.palette.primary.main,
                ODD_OPACITY + theme.palette.action.selectedOpacity,
            ),
            '&:hover': {
                backgroundColor: alpha(
                    theme.palette.primary.main,
                    ODD_OPACITY +
                    theme.palette.action.selectedOpacity +
                    theme.palette.action.hoverOpacity,
                ),
                // Reset on touch devices, it doesn't add specificity
                '@media (hover: none)': {
                    backgroundColor: alpha(
                        theme.palette.primary.main,
                        ODD_OPACITY + theme.palette.action.selectedOpacity,
                    ),
                },
            },
        },
    },
    [`& .${gridClasses.cell}:focus`]: {
        outline: 'none !important',
    },
    [`& .${gridClasses.cell}:focus-within`]: {
        outline: 'none !important',
    },
    [`& .${gridClasses.columnHeader}:focus`]: {
        outline: 'none !important',
    },
    [`& .${gridClasses.columnHeader}:focus-within`]: {
        outline: 'none !important',
    },
    // [`& .${gridClasses.columnHeaderTitleContainerContent}`]: {
    //     flexGrow: 1
    // },
}));

export default DataGridCustomStyle;