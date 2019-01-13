package bankingservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeinindia.controller.R;
import java.util.List;

import bankingservice.model.BankModel;

public class BankListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private final Activity context;
    private List<BankModel> bankModelList;

    public BankListAdapter(Activity context, List<BankModel> bankModels) {
        super();
        this.context = context;
        bankModelList = bankModels;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.bank_list_adapter, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.textView1);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView1);
            convertView.setTag(viewHolder);

        }

        ViewHolder holder = (ViewHolder) convertView.getTag();


        String s = bankModelList.get(position).getBank_name();
        String img = bankModelList.get(position).getBank_name();
        if (holder != null && s != null && img != null) {
            holder.text.setText(s);

            switch (position) {
                case 0:
                    holder.image.setImageResource(R.drawable.state_bank_of_india);
                    break;
                case 1:
                    holder.image.setImageResource(R.drawable.bank_of_baroda);
                    break;
                case 2:
                    holder.image.setImageResource(R.drawable.idbi_bank);
                    break;
                case 3:
                    holder.image.setImageResource(R.drawable.central_bank_of_india);
                    break;
                case 4:
                    holder.image.setImageResource(R.drawable.hdfc_bank);
                    break;
                case 5:
                    holder.image.setImageResource(R.drawable.citi_bank);
                    break;
                case 6:
                    holder.image.setImageResource(R.drawable.axis_bank);
                    break;
                case 7:
                    holder.image.setImageResource(R.drawable.kotak_bank);
                    break;
                case 8:
                    holder.image.setImageResource(R.drawable.yes_bank);
                    break;
                case 9:
                    holder.image.setImageResource(R.drawable.punjab_national_bank);
                    break;
                case 10:
                    holder.image.setImageResource(R.drawable.dena_bank);
                    break;
                case 11:
                    holder.image.setImageResource(R.drawable.canara_bank);
                    break;
                case 12:
                    holder.image.setImageResource(R.drawable.bank_of_india);
                    break;
                case 13:
                    holder.image.setImageResource(R.drawable.corporation_bank);
                    break;
                case 14:
                    holder.image.setImageResource(R.drawable.union_bank_of_india);
                    break;
                case 15:
                    holder.image.setImageResource(R.drawable.uco_bank);
                    break;
                case 16:
                    holder.image.setImageResource(R.drawable.vijaya_bank);
                    break;
                case 17:
                    holder.image.setImageResource(R.drawable.south_indian_bank);
                    break;
                case 18:
                    holder.image.setImageResource(R.drawable.american_express);
                    break;
                case 19:
                    holder.image.setImageResource(R.drawable.hsbc_bank);
                    break;
                case 20:
                    holder.image.setImageResource(R.drawable.federal_bank);
                    break;
                case 21:
                    holder.image.setImageResource(R.drawable.indian_overseas_bank);
                    break;
                case 22:
                    holder.image.setImageResource(R.drawable.ing_bank);
                    break;
                case 23:
                    holder.image.setImageResource(R.drawable.karur_vysya_bank);
                    break;
                case 24:
                    holder.image.setImageResource(R.drawable.abn_amro);
                    break;
                case 25:
                    holder.image.setImageResource(R.drawable.allhabad_bank);
                    break;
                case 26:
                    holder.image.setImageResource(R.drawable.andhra_bank);
                    break;
                case 27:
                    holder.image.setImageResource(R.drawable.anz_bank);
                    break;
                case 28:
                    holder.image.setImageResource(R.drawable.bank_of_maharashtra);
                    break;
                case 29:
                    holder.image.setImageResource(R.drawable.barclays_bank);
                    break;
                case 30:
                    holder.image.setImageResource(R.drawable.indian_bank);
                    break;
                case 31:
                    holder.image.setImageResource(R.drawable.bharatiya_mahila_bank);
                    break;
                case 32:
                    holder.image.setImageResource(R.drawable.punjab_and_sind_bank);
                    break;
                case 33:
                    holder.image.setImageResource(R.drawable.cashnet_bank);
                    break;
                case 34:
                    holder.image.setImageResource(R.drawable.saraswat_bank);
                    break;
                case 35:
                    holder.image.setImageResource(R.drawable.centurion_bank_of_punjab);
                    break;
                case 36:
                    holder.image.setImageResource(R.drawable.standard_chartered_bank);
                    break;
                case 37:
                    holder.image.setImageResource(R.drawable.state_bank_of_bikaner_and_jaipur);
                    break;
                case 38:
                    holder.image.setImageResource(R.drawable.deutsche_bank);
                    break;
                case 39:
                    holder.image.setImageResource(R.drawable.state_bank_of_travancore);
                    break;
                case 40:
                    holder.image.setImageResource(R.drawable.syndicate_bank);
                    break;
                case 41:
                    holder.image.setImageResource(R.drawable.dhanlaxmi_bank);
                    break;
                case 42:
                    holder.image.setImageResource(R.drawable.united_bank_of_india);
                    break;
                case 43:
                    holder.image.setImageResource(R.drawable.karnataka_bank);
                    break;
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return bankModelList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }
} 