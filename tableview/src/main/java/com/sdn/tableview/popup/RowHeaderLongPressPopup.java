/*
 * Copyright (c) 2018. Evren Coşkun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.sdn.tableview.popup;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.ITableView;

/**
 * Created by evrencoskun on 21.01.2018.
 */

public class RowHeaderLongPressPopup extends PopupMenu implements PopupMenu.OnMenuItemClickListener {

    // Menu Item constants
    private static final int SCROLL_COLUMN = 1;
    private static final int SHOWHIDE_COLUMN = 2;
    private static final int REMOVE_ROW = 3;

    private ITableView mTableView;
    private Context mContext;
    private int mRowPosition;

    public RowHeaderLongPressPopup(RecyclerView.ViewHolder viewHolder, ITableView tableView) {
        super(viewHolder.itemView.getContext(), viewHolder.itemView);

        this.mTableView = tableView;
        this.mContext = viewHolder.itemView.getContext();
        this.mRowPosition = viewHolder.getAbsoluteAdapterPosition();//.getAdapterPosition();

        initialize();
    }

    private void initialize() {
        createMenuItem();

        this.setOnMenuItemClickListener(this);
    }

    private void createMenuItem() {
        this.getMenu().add(Menu.NONE, SCROLL_COLUMN, 0, "Scroll " + mRowPosition + " position");
        this.getMenu().add(Menu.NONE, SHOWHIDE_COLUMN, 1, "Hide " + mRowPosition + " position");
        this.getMenu().add(Menu.NONE, REMOVE_ROW, 2, "Remove " + mRowPosition + " position");
        // add new one ...

    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        // Note: item id is index of menu item..

        switch (menuItem.getItemId()) {
            case SCROLL_COLUMN:
                mTableView.scrollToColumnPosition(15);
                break;
            case SHOWHIDE_COLUMN:
                int column = 1;
                if (mTableView.isColumnVisible(column)) {
                    mTableView.hideColumn(column);
                } else {
                    mTableView.showColumn(column);
                }

                break;
            case REMOVE_ROW:
                mTableView.getAdapter().removeRow(mRowPosition);
                break;
        }
        return true;
    }

}
