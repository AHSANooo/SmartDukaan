import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdukaan.Item
import com.example.smartdukaan.R

class ItemAdapter(
    private val items: List<Item>,
    private val onClick: (Item) -> Unit   // Correct
) : RecyclerView.Adapter<ItemAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = "Rs ${item.sellingPrice}"
        holder.tvQty.text = "${item.qty}"

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
