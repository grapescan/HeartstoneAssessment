package me.grapescan.cards.ui.details

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import me.grapescan.cards.R
import me.grapescan.cards.data.Card
import me.grapescan.cards.ext.StringExtra
import me.grapescan.cards.ui.glide.GlideApp
import me.grapescan.cards.ui.widget.CheckableImageView
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CardDetailsActivity : AppCompatActivity() {

    companion object {

        private var Intent.cardId by StringExtra()

        fun createIntent(context: Context, cardId: String) = Intent(context, CardDetailsActivity::class.java).apply {
            this.cardId = cardId
        }
    }

    private val viewModel: CardDetailsViewModel by inject(parameters = { parametersOf(intent.cardId) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        viewModel.card.observe(this, Observer<Card> {
            findViewById<CheckableImageView>(R.id.favorite).setCheckedSilent(it.isFavorite)
            findViewById<ImageView>(R.id.content).let { card ->
                GlideApp.with(this@CardDetailsActivity)
                    .load(it.imageUrl)
                    .into(object : DrawableImageViewTarget(card) {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            super.onResourceReady(resource, transition)
                            supportStartPostponedEnterTransition()
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            supportStartPostponedEnterTransition()
                        }
                    })
            }
        })
        findViewById<ImageView>(R.id.back).setOnClickListener { supportFinishAfterTransition() }
        findViewById<CheckableImageView>(R.id.favorite).setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFavorite(intent.cardId, isChecked)
        }
        supportPostponeEnterTransition()

    }
}
