package com.rocpjunior.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rocpjunior.whatsappfirebase.R
import com.rocpjunior.whatsappfirebase.adapters.ViewPagerAdapter
import com.rocpjunior.whatsappfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        toolbar()
        navegacaoAbas()
    }

    private fun navegacaoAbas() {
        val tabLayout = binding.tabLayoutInicial
        val viewPager = binding.viewPagerInicial

        val abas = listOf("CONVERSAS", "CONTATOS")
        viewPager.adapter = ViewPagerAdapter(
            abas, supportFragmentManager, lifecycle
        )

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager) { aba, posicao ->
            aba.text = abas[posicao]
        }.attach()
    }

    private fun toolbar() {
        val toolbar = binding.includeMainToolbar.materialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp"
        }
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_inicial, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when( menuItem.itemId){
                        R.id.item_perfil -> {
                            startActivity(
                                Intent(applicationContext, Perfil::class.java)
                            )
                        }
                        R.id.item_sair -> {
                        desconectarUsuario()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun desconectarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Desconectando")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não"){dialog, posicao -> }
            .setPositiveButton("Sim"){dialog, posicao ->
                firebaseAuth.signOut()
                Toast.makeText(applicationContext, "Login desconectado com sucesso =D", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(applicationContext, Login::class.java)
                )
            }
            .create()
            .show()
    }
}