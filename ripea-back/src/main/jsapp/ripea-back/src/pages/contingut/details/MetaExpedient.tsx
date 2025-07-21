import {Grid, Typography} from "@mui/material";
import React from "react";

const style = {
    display: 'flex',
    alignItems: 'center',
    width: 'max-content',
    padding: '1px 4px',
    fontSize: '11px',
    fontWeight: '500',
    borderRadius: '2px',
    color: 'white',
    backgroundColor: 'info.main',
}

export const MultiplicitatStyled = (props:any) => {
    const {multiplicitat} = props;

    return <Typography variant="caption" sx={style}>
        {multiplicitat}
    </Typography>
}

const MetaExpedient = (props:any) => {
    const {entity} = props;

    return <Grid display={"flex"} alignItems={"center"} justifyContent={'space-between'}>
        {entity?.nom}
        <MultiplicitatStyled multiplicitat={entity?.multiplicitat}/>
    </Grid>
}
export default MetaExpedient;