import useAnotacioDetail from "./AnotacioDetail.tsx";

const useAnotacioActions = (refresh?: () => void) => {
    // const {
    //     action: apiAction
    // } = useResourceApiService('expedientPeticioResource');

    // const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    // const confirmDialogButtons = useConfirmDialogButtons();
    // const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {handleOpen, dialog} = useAnotacioDetail();

    const actions = [
        {
            title: "Detalle",
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
    ];

    const components = <>
        {dialog}
    </>;

    return {
        actions,
        components
    }
}
export default useAnotacioActions;