import {Box, Tab, Tabs} from "@mui/material";
import React from "react";
import {StyledBadge} from "./StyledBadge.tsx";

type TabProps = {
    value: string;
    label: string;
    content: any;
    badge?: string;
    badgeColor?: 'primary' | 'secondary' | 'default' | 'error' | 'info' | 'success' | 'warning' | undefined;
    hidden?: boolean;
};

const TabPanel = (props:any) => {
    const { children, value, index, ...other } = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            style={{height: '100%'}}
            {...other}
        >
            {value === index && (
                <Box p={2} height={'100%'}>
                    {children}
                </Box>
            )}
        </div>
    );
}

const TabComponent = (props :any) => {
    const { tabs, headerAdditionalData, ...other}=props;
    const [value, setValue] = React.useState(tabs[0].value);

    const handleChange = (event :any, newValue :string) : void => {
        setValue(newValue);
    };

    return <Box sx={{display: 'flex', flexDirection: 'column', height: '100%'}}>
        <Box sx={{flexGrow: 1, display: 'flex', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', width: '100%'}}>
            <Tabs
                value={value}
                onChange={handleChange}
                {...other}
                sx={{px: 1}}
            >
                {tabs.filter((tab:TabProps)=>!tab.hidden).map((tab:TabProps) => {
                    const {value, label , content, badge, badgeColor= 'primary'} = tab;

                    return <Tab value={value} content={content} key={"tab-" + value} label={
                        <StyledBadge badgeContent={badge} badgecolor={badgeColor}>{label}</StyledBadge>}/>
                })}
            </Tabs>
            {headerAdditionalData}
        </Box>
        {tabs.map((tab:TabProps) =>
            <TabPanel value={value} index={tab.value} key={"tab-panel-"+tab.value}>
                {tab.content}
            </TabPanel>
        )}
    </Box>
}

export default TabComponent;