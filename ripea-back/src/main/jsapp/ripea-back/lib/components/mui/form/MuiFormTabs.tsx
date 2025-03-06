import React from 'react';
import Tabs from '@mui/material/Tabs';
import Tab, { TabProps } from '@mui/material/Tab';
import Box from '@mui/material/Box';
import { useBaseAppContext } from '../../BaseAppContext';
import { useFormContext } from '../../form/FormContext';

interface FormTabContentProps {
    index: number;
    currentIndex: number;
    showOnCreate?: boolean;
    children?: React.ReactNode;
};

export type FormTabsValue = number | string | TabProps;

interface FormTabsProps {
    tabs: FormTabsValue[];
    currentIndex?: number;
    onIndexChange?: (index: number) => void;
    tabIndexesWithGrids?: number[];
};

export const MuiFormTabContent: React.FC<FormTabContentProps> = (props) => {
    const {
        index,
        currentIndex,
        showOnCreate,
        children,
        ...other
    } = props;
    const { id } = useFormContext();
    return id != null ? <div
        role='tabpanel'
        hidden={currentIndex !== index}
        id={`tabpanel-${index}`}
        aria-labelledby={`tab-${index}`}
        style={{ height: '100%' }}
        {...other}>
        {currentIndex === index && <Box sx={{ pt: 3, pb: 2, height: '100%' }}>{children}</Box>}
    </div> : (showOnCreate ? children : null);
}

export const MuiFormTabs: React.FC<FormTabsProps> = (props) => {
    const {
        tabs,
        currentIndex,
        onIndexChange,
        tabIndexesWithGrids,
    } = props;
    const { id } = useFormContext();
    const { setContentExpandsToAvailableHeight } = useBaseAppContext();
    const gridCheck = (index: number) => {
        if (tabIndexesWithGrids != null) {
            const isGridTab = tabIndexesWithGrids?.includes(index) ?? false;
            setContentExpandsToAvailableHeight(isGridTab);
        }
    }
    const handleIndexChange = (_event: React.SyntheticEvent, value: any) => {
        value && gridCheck(value);
        onIndexChange?.(value);
    }
    React.useEffect(() => {
        currentIndex && gridCheck(currentIndex);
    }, [currentIndex]);
    const tabsHeightFix = { minHeight: '48px' };
    return id != null ? <Tabs
        value={currentIndex}
        onChange={handleIndexChange}
        sx={{ borderBottom: '1px solid rgba(0, 0, 0, 0.23)' }}>
        {tabs.map((t, i) => {
            if (typeof t === 'string') {
                return <Tab key={i} value={i} label={t} sx={tabsHeightFix} />
            } else {
                return <Tab {...(t as any)} key={i} value={i} sx={tabsHeightFix} />
            }
        })}
    </Tabs> : null;
}

export default MuiFormTabs;