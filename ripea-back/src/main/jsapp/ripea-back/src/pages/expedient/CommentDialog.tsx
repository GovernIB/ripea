import {Dialog} from "reactlib";
import {useState} from "react";
import {Button, Icon} from "@mui/material";

const CommentDialog = (props:any) => {
    const { entity } = props
    const [open, setOpen] = useState(false);

    const handleClose = () => {
        setOpen(false);
    };

    return <>
        <Button variant="outlined" sx={{borderRadius: 1}} color={"inherit"} onClick={()=>setOpen(true)}><Icon>forum</Icon>{entity?.numComentaris}</Button>
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={`Comentarios del expediente: ${entity?.nom}`}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={[
                {
                    value: 'close',
                    text: 'Close'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            {/*<Form resourceName={""}>*/}
            {/*    <FormField name={"message"} type={"text"}></FormField>*/}
            {/*</Form>*/}
        </Dialog>
    </>
}
export default CommentDialog;