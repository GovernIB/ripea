import React from 'react';
import DataNoRows from '../datacommon/DataNoRows';

type DataGridNoRowsOverlayProps = {
    findDisabled?: boolean;
    noRowsText?: string;
};

const DataGridNoRowsOverlay: React.FC<DataGridNoRowsOverlayProps> = (props) => {
    const { findDisabled, noRowsText } = props;
    return <DataNoRows findDisabled={findDisabled} noRowsText={noRowsText} />;
}

export default DataGridNoRowsOverlay;