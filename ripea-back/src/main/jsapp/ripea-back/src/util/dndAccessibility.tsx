import React from 'react';

// Wraps the default dnd-kit screen reader instructions in <p> elements to satisfy
// WCAG 2.1 AA (SC 1.3.1): text blocks must use proper semantic markup.
export const dndScreenReaderInstructions = {
    draggable: (
        <>
            <p>To pick up a draggable item, press the space bar.</p>
            <p>While dragging, use the arrow keys to move the item.</p>
            <p>Press space again to drop the item in its new position, or press escape to cancel.</p>
        </>
    ) as unknown as string,
};
