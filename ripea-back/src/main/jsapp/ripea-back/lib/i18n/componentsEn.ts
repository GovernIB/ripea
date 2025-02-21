const componentsEn = {
    app: {
        menu: {
            home: 'Home',
        },
        auth: {
            logout: 'Log out',
        },
        error: {
            '404': 'Not found',
            '503': 'No connection',
        },
        offline: {
            message: 'Server connection lost',
        }
    },
    grid: {
        refresh: {
            title: 'Refresh',
        },
        create: {
            title: 'Create',
        },
        update: {
            title: 'Update',
        },
        delete: {
            title: 'Delete',
            single: {
                title: 'Delete item',
                confirm: 'Are you sure you want to delete this item (this action can\'t be undone)?',
                success: 'Deleted items',
                error: 'Error deleting items',
            },
        },
    },
    form: {
        goBack: {
            title: 'Go back',
        },
        revert: {
            title: 'Undo changes',
            confirm: 'Are you sure you want to revert the changes in the form?',
        },
        create: {
            title: 'Create',
            success: 'Element created',
            error: 'Error creating element',
        },
        update: {
            title: 'Update',
            success: 'Element updated',
            error: 'Error updating element',
            wrong_resource_type: 'Couldn\'t save forms with resource type "{{resourceType}}"',
        },
        delete: {
            title: 'Delete',
            confirm: 'Are you sure you want to delete this item (this action can\'t be undone)?',
            success: 'Element deleted',
            error: 'Error deleting element',
        },
    },
    buttons: {
        answerRequired: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        confirm: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        form: {
            save: 'Save',
            cancel: 'Cancel',
        },
        action: {
            exec: 'Execute',
            cancel: 'Cancel',
        },
        report: {
            generate: 'Generate',
            cancel: 'Cancel',
        },
        close: {
            close: 'Close'
        },
        misc: {
            retry: 'Retry',
        },
    },
};

export default componentsEn;