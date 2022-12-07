package ru.brightos.oop8.ui.tree

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolder
import com.amrdeveloper.treeview.TreeViewHolderFactory
import dagger.hilt.android.AndroidEntryPoint
import ru.brightos.oop8.R
import ru.brightos.oop8.databinding.FragmentTreeBinding
import ru.brightos.oop8.model.CGroup
import ru.brightos.oop8.model.CShape
import ru.brightos.oop8.observable.TreeObservable

@AndroidEntryPoint
class TreeFragment : Fragment() {

    private lateinit var binding: FragmentTreeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = object : TreeViewHolderFactory {
            override fun getTreeViewHolder(view: View, layout: Int): TreeViewHolder {
                return CustomViewHolder(view)
            }
        }

        val treeViewAdapter = TreeViewAdapter(factory)
        binding.root.layoutManager = LinearLayoutManager(context)
        binding.root.adapter = treeViewAdapter

        treeViewAdapter.setTreeNodeClickListener { treeNode, view ->
            var operableTreeNode = treeNode
            println("hmst")
            if (treeNode.value is CShape) {
                while (operableTreeNode.parent != null)
                    operableTreeNode = operableTreeNode.parent

                changeChildrenSelection(
                    operableTreeNode,
                    !(operableTreeNode.value as CShape).selected
                )

                TreeObservable.instance.notifyObservers()
            }
        }

        TreeObservable.instance.registerObserver {
            println("lol")
            val roots = arrayListOf<TreeNode>()
            it.forEach { selectableView ->
                TreeNode(selectableView.shape, R.layout.layout_tree_item).let {
                    roots.add(it)
                    if (it.value is CGroup)
                        (it.value as CGroup).apply {
                            shapesList.forEach { shape ->
                                processNode(it, shape, selected)
                            }
                        }
                }
            }
            treeViewAdapter.updateTreeNodes(roots)
            treeViewAdapter.expandAll()
        }
        TreeObservable.instance.notifyObservers()
    }

    fun processNode(tn: TreeNode, o: CShape, select: Boolean) {
        o.selected = select
        val child = TreeNode(o, R.layout.layout_tree_item)
        tn.addChild(child)
        if (o is CGroup)
            o.shapesList.forEach {
                processNode(child, it, select)
            }
    }

    fun changeChildrenSelection(tn: TreeNode, select: Boolean) {
        (tn.value as CShape).selected = select
        tn.children.forEach {
            changeChildrenSelection(it, select)
        }
    }

    class CustomViewHolder(itemView: View) : TreeViewHolder(itemView) {
        override fun bindTreeNode(node: TreeNode) {
            super.bindTreeNode(node)

            val textView = itemView.findViewById<TextView>(R.id.text)

            when (node.value) {
                is String -> {
                    textView.text = node.value as String
                    println("!! ${node.value}")
                    textView.setTypeface(null, Typeface.BOLD_ITALIC)
                }
                is CShape -> {
                    val shape = node.value as CShape

                    textView.text = shape.typeName
                    println("!! ${shape.typeName}")
                    textView.setTypeface(
                        null,
                        if (shape.selected)
                            Typeface.BOLD
                        else
                            Typeface.NORMAL
                    )
                    textView.setTextColor(
                        if (shape.selected)
                            Color.parseColor("#FFFFFF")
                        else
                            Color.parseColor("#A0A0A0")
                    )
                }
            }
        }
    }
}