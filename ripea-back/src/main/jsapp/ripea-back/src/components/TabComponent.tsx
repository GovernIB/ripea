import {Box, Tab, Tabs} from "@mui/material";
import React from "react";
import Badge from "@mui/material/Badge";

type TabProps = {
    value: string;
    label: string;
    content: any;
    badge?: string;
    badgeColor?: 'primary' | 'secondary' | 'default' | 'error' | 'info' | 'success' | 'warning' | undefined;
    hidden?: boolean;
};
type TabComponentProps = {
    tabs: TabProps[];
    headerAdditionalData?:any;
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

const TabComponent: React.FC<TabComponentProps> = (props :TabComponentProps) => {
    const { tabs,headerAdditionalData, ...other}=props;
    const [value, setValue] = React.useState(tabs[0].value);

    const handleChange = (event :any, newValue :string) : void => {
        setValue(newValue);
    };

    return <Box sx={{display: 'flex', flexDirection: 'column', height: '100%'}}>
        <Tabs
            value={value}
            onChange={handleChange}
            {...other}
            sx={{px: 1}}
        >
            {tabs.filter((tab)=>!tab.hidden).map((tab) => {
                const {value, label , content, badge, badgeColor= 'primary'} = tab;

                return <Tab value={value} content={content} key={"tab-" + value} label={
                    <Badge badgeContent={badge} color={badgeColor} sx={{px: 1}}>{label}</Badge>}/>
            })}

            {headerAdditionalData}
        </Tabs>

        {tabs.map((tab) =>
            <TabPanel value={value} index={tab.value} key={"tab-panel-"+tab.value}>
                {tab.content}
            </TabPanel>
        )}
    </Box>
}

export default TabComponent;