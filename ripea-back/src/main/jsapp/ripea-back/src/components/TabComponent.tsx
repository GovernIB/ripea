import {Box, Tab, Tabs} from "@mui/material";
import React from "react";

type TabProps = {
    value: string;
    label: string;
    content: any;
};
type TabComponentProps = {
    tabs: TabProps[];
};

function TabPanel(props) {
    const { children, value, index, ...other } = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box p={2}>
                    {children}
                </Box>
            )}
        </div>
    );
}

const TabComponent: React.FC<TabComponentProps> = (props :TabComponentProps) => {
    const { tabs, ...other}=props;
    const [value, setValue] = React.useState(tabs[0].value);

    const handleChange = (event :any, newValue :string) : void => {
        setValue(newValue);
    };

    return <>
        <Tabs
            value={value}
            onChange={handleChange}
            {...other}
            sx={{px: 1}}
        >
            {tabs.map((tab) => <Tab label={tab.label} value={tab.value} key={"tab-"+tab.value}/>)}
        </Tabs>

        {tabs.map((tab) =>
            <TabPanel value={value} index={tab.value} key={"tab-panel-"+tab.value}>
                {tab.content}
            </TabPanel>
        )}
    </>
}

export default TabComponent;