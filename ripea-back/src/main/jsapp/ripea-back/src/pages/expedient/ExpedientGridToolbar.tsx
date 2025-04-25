import React from 'react';
import { Box, Button, ButtonGroup, Chip, Icon, ListItemIcon, ListItemText, Menu, MenuItem, Tooltip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { MuiDataGridApi } from 'reactlib';
import { GridApiCommunity } from '@mui/x-data-grid';
import {GridApiPro} from "@mui/x-data-grid-pro";

interface ExpedientGridToolbarProps {
    selectedRows: any[];
    setSelectedRows: (rows: any[]) => void;
    gridRows: any[];
    apiRef: React.MutableRefObject<MuiDataGridApi | undefined>;
    datagridApiRef: React.MutableRefObject<GridApiPro>
}

const ExpedientGridToolbar: React.FC<ExpedientGridToolbarProps> = ({
    selectedRows,
    setSelectedRows,
    gridRows,
    apiRef,
    datagridApiRef,
}) => {
    const { t } = useTranslation();
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);

    // Handle selection actions
    const handleSelectAll = () => {
        // Get all row IDs from the grid rows
        if (gridRows && gridRows.length > 0) {
            // Use all rows from the grid, not just the current page
            const allIds = gridRows.map((row: any) => row.id);
            setSelectedRows(allIds);
            datagridApiRef.current.setRowSelectionModel(allIds);
        }
    };

    const handleClearSelection = () => {
        setSelectedRows([]);
        datagridApiRef.current.setRowSelectionModel([]);
    };

    // Handle dropdown menu
    const handleMenuClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
    };

    // Handle refresh
    const refresh = () => {
        apiRef?.current?.refresh?.();
    };

    return (
        <Box sx={{ display: 'flex', justifyContent: 'end', alignItems: 'flex-start', padding: '8px 16px', borderBottom: '1px solid rgba(224, 224, 224, 1)' }}>

            <Box sx={{ display: 'flex', alignItems: 'flex-start' }}>
                {/* Selection buttons */}
                <ButtonGroup
                    variant="outlined"
                    size="small"
                    sx={{
                        '& .MuiButton-root': {
                            borderColor: 'rgba(0, 0, 0, 0.23)' // Standard MUI outlined button border color
                        }
                    }}>
                    <Tooltip title={t('Seleccionar tots')}>
                        <Button
                            onClick={handleSelectAll}
                            sx={{ minWidth: '40px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: 0.75 }}
                        >
                            <Icon color="action">check_box</Icon>
                        </Button>
                    </Tooltip>
                    <Tooltip title={t('Netejar selecció')}>
                        <Button
                            onClick={handleClearSelection}
                            sx={{ minWidth: '40px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: 0.75 }}
                        >
                            <Icon color="action">check_box_outline_blank</Icon>
                        </Button>
                    </Tooltip>
                    <Button
                        onClick={handleMenuClick}
                        startIcon={<Chip label={selectedRows.length} size="small" />}
                        endIcon={<Icon>arrow_drop_down</Icon>}
                        disabled={selectedRows.length === 0}
                    >
                        <Box sx={{ marginLeft: 1 }}>Opcions</Box>
                    </Button>
                </ButtonGroup>
                <Menu
                    anchorEl={anchorEl}
                    open={open}
                    onClose={handleMenuClose}
                >
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>lock</Icon></ListItemIcon>
                        <ListItemText>Agafar</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>lock_open</Icon></ListItemIcon>
                        <ListItemText>Alliberar</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>person_add_alt1</Icon></ListItemIcon>
                        <ListItemText>Seguir</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>person_remove</Icon></ListItemIcon>
                        <ListItemText>Deixar de seguir</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>delete</Icon></ListItemIcon>
                        <ListItemText>Esborrar</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>download</Icon></ListItemIcon>
                        <ListItemText>Exportar full de càlcul</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>download</Icon></ListItemIcon>
                        <ListItemText>Exportar CSV</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>download</Icon></ListItemIcon>
                        <ListItemText>Exportar índex ZIP</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>download</Icon></ListItemIcon>
                        <ListItemText>Exportar índex PDF</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>download</Icon></ListItemIcon>
                        <ListItemText>Exportació ENI</ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon><Icon>description</Icon></ListItemIcon>
                        <ListItemText>Exportar els documents dels expedients seleccionats</ListItemText>
                    </MenuItem>
                </Menu>

                {/* Refresh button */}
                <Tooltip title={t('common.refresh')}>
                    <Button
                        variant="outlined"
                        size="small"
                        color="info"
                        startIcon={<Icon>refresh</Icon>}
                        onClick={refresh}
                        sx={{ borderRadius: '4px', padding: '6px 4px 6px 8px', minWidth: '20px' }}
                    >
                    </Button>
                </Tooltip>
                {/* Create button */}
                <Button
                    variant="outlined"
                    size="small"
                    startIcon={<Icon>add</Icon>}
                    onClick={() => apiRef?.current?.showCreateDialog?.()}
                    sx={{ borderRadius: '4px' }}
                >
                    {t('page.expedient.nou')}
                </Button>
            </Box>
        </Box>
    );
};

export default ExpedientGridToolbar;
