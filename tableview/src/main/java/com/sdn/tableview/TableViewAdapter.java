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

package com.sdn.tableview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.evrencoskun.tableview.sort.SortState;
import com.sdn.tableview.holder.CellViewHolder;
import com.sdn.tableview.holder.ColumnHeaderViewHolder;
import com.sdn.tableview.holder.RowHeaderViewHolder;
import com.sdn.tableview.model.Cell;
import com.sdn.tableview.model.ColumnHeader;
import com.sdn.tableview.model.RowHeader;

/**
 * Created by evrencoskun on 11/06/2017.
 * <p>
 * This is a sample of custom TableView Adapter.
 */

public class TableViewAdapter extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {

    public static boolean ON_CORNER_LISTENER=true;
    // Cell View Types by Column Position
    private static final int MOOD_CELL_TYPE = 1;
    private static final int GENDER_CELL_TYPE = 2;
    // add new one if it necessary..

    private static final String LOG_TAG = TableViewAdapter.class.getSimpleName();

    private TableViewModel mTableViewModel;
    private final LayoutInflater mInflater;

    public TableViewAdapter(Context context, TableViewModel tableViewModel) {
        super(context);
        this.mTableViewModel = tableViewModel;
        this.mInflater = LayoutInflater.from(mContext);

    }

    /**
     * Aquie es donde ser crea Su Cell ViewHolder.Este metodo es llamado cuando Cell RecyclerView
     *  de TableView necesita un nuevo RecyclerView.ViewHolder del tipo dado para repesentar un Elemento
     *
     * @param viewType : Este valor viene del metodo "getCellItemViewType"
     *                para admitir diferentes tipos de viewHolder como un elemento de celda.
     *
     * @see #getCellItemViewType(int);
     */
    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        //TODO check
        //Log.e(LOG_TAG, " onCreateCellViewHolder has been called");
        View layout;
        layout = mInflater.inflate(R.layout.table_view_cell_layout, parent, false);

        // Create a Cell ViewHolder
        return new CellViewHolder(layout);
    }

    /**
     * Aquí es donde configuras los datos del modelo de vista de celda en su Cell ViewHolder
     * personalizado. Este método es llamado por Cell RecyclerView de TableView para mostrar los
     * datos en la posición especificada
     *
     * Este método le brinda Todo lo que necesita sobre un elemento de celda.
     *
     * @param holder         : Este es uno de tus cell ViewHolders que fue creado en el metodo
     *                       ```onCreateCellViewHolder```. En este ejemplo  hemos creado "CellViewHolder" holder.
     *
     * @param cellItemModel  : This is the cell view model located on this X and Y position. In this example, the model class is "Cell".
     * @param columnPosition : This is the X (Column) position of the cell item.
     * @param rowPosition    : This is the Y (Row) position of the cell item.
     *
     * @see #onCreateCellViewHolder(ViewGroup, int) ;
     */
    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object cellItemModel, int columnPosition, int rowPosition) {
        Cell cell = (Cell) cellItemModel;
        //Log.e(LOG_TAG, cell.getData()+ " " +holder.getItemViewType());
        CellViewHolder viewHolder = (CellViewHolder) holder;
        viewHolder.setCell(cell);
    }

    /**
     * This is where you create your custom Column Header ViewHolder. This method is called when
     * Column Header RecyclerView of the TableView needs a new RecyclerView.ViewHolder of the given
     * type to represent an item.
     *
     * @param viewType : This value comes from "getColumnHeaderItemViewType" method to support
     *                 different type of viewHolder as a Column Header item.
     *
     * @see #getColumnHeaderItemViewType(int);
     */
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        // TODO: check
      // Log.e(LOG_TAG, " onCreateColumnHeaderViewHolder has been called");
        // Get Column Header xml Layout
        View layout = mInflater.inflate(R.layout.table_view_column_header_layout, parent, false);

        // Create a ColumnHeader ViewHolder
        return new ColumnHeaderViewHolder(layout, getTableView());
    }

    /**
     * That is where you set Column Header View Model data to your custom Column Header ViewHolder.
     * This method is Called by ColumnHeader RecyclerView of the TableView to display the data at
     * the specified position. This method gives you everything you need about a column header
     * item.
     *
     * @param holder                : This is one of your column header ViewHolders that was created
     *                              on ```onCreateColumnHeaderViewHolder``` method. In this example
     *                              we have created "ColumnHeaderViewHolder" holder.
     * @param columnHeaderItemModel : This is the column header view model located on this X
     *                              position. In this example, the model class is "ColumnHeader".
     * @param columnPosition        : This is the X (Column) position of the column header item.
     *
     * @see #onCreateColumnHeaderViewHolder(ViewGroup, int) ;
     */
    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object columnHeaderItemModel, int columnPosition) {
        ColumnHeader columnHeader = (ColumnHeader) columnHeaderItemModel;

        // Get the holder to update cell item text
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.setColumnHeader(columnHeader);
    }

    /**
     * This is where you create your custom Row Header ViewHolder. This method is called when
     * Row Header RecyclerView of the TableView needs a new RecyclerView.ViewHolder of the given
     * type to represent an item.
     *
     * @param viewType : This value comes from "getRowHeaderItemViewType" method to support
     *                 different type of viewHolder as a row Header item.
     *
     * @see #getRowHeaderItemViewType(int);
     */
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        // Get Row Header xml Layout
        View layout = mInflater.inflate(R.layout.table_view_row_header_layout, parent, false);
        // Create a Row Header ViewHolder
        return new RowHeaderViewHolder(layout);
    }


    /**
     * That is where you set Row Header View Model data to your custom Row Header ViewHolder. This
     * method is Called by RowHeader RecyclerView of the TableView to display the data at the
     * specified position. This method gives you everything you need about a row header item.
     *
     * @param holder             : This is one of your row header ViewHolders that was created on
     *                           ```onCreateRowHeaderViewHolder``` method. In this example we have
     *                           created "RowHeaderViewHolder" holder.
     * @param rowHeaderItemModel : This is the row header view model located on this Y position. In
     *                           this example, the model class is "RowHeader".
     * @param rowPosition        : This is the Y (row) position of the row header item.
     *
     * @see #onCreateRowHeaderViewHolder(ViewGroup, int) ;
     */
    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object rowHeaderItemModel,int rowPosition) {
        RowHeader rowHeader = (RowHeader) rowHeaderItemModel;

        // Get the holder to update row header item text
        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.row_header_textview.setText(String.valueOf(rowHeader.getData()));
    }


    @Override
    public View onCreateCornerView() {
        // Get Corner xml layout
        View corner = mInflater.inflate(R.layout.table_view_corner_layout, null);
        ((TextView) corner.findViewById(R.id.lblCornerTable)).setText(""+mTableViewModel.Filas.size());


        if(ON_CORNER_LISTENER){
            corner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SortState sortState = TableViewAdapter.this.getTableView().getRowHeaderSortingStatus();

                    if (sortState != SortState.ASCENDING) {
                        // Log.d("TableViewAdapter", "Order Ascending");
                        TableViewAdapter.this.getTableView().sortRowHeader(SortState.ASCENDING);
                    } else {
                        //Log.d("TableViewAdapter", "Order Descending");
                        TableViewAdapter.this.getTableView().sortRowHeader(SortState.DESCENDING);
                    }
                }
            });
        }

        return corner;
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        // The unique ID for this type of column header item
        // If you have different items for Cell View by X (Column) position,
        // then you should fill this method to be able create different
        // type of CellViewHolder on "onCreateCellViewHolder"
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        // The unique ID for this type of row header item
        // If you have different items for Row Header View by Y (Row) position,
        // then you should fill this method to be able create different
        // type of RowHeaderViewHolder on "onCreateRowHeaderViewHolder"
        return 0;
    }

    @Override
    public int getCellItemViewType(int column) {

        // The unique ID for this type of cell item
        // If you have different items for Cell View by X (Column) position,
        // then you should fill this method to be able create different
        // type of CellViewHolder on "onCreateCellViewHolder"
        /*switch (column) {
            case TableViewModel.MOOD_COLUMN_INDEX:
                return MOOD_CELL_TYPE;
            case TableViewModel.GENDER_COLUMN_INDEX:
                return GENDER_CELL_TYPE;
            default:
                // Default view type
                return 0;
        }*/
        return 0;
    }
}
