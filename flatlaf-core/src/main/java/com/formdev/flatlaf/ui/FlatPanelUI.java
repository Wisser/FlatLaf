/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.ui;

import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPanel}.
 *
 * <!-- BasicPanelUI -->
 *
 * @uiDefault Panel.font				Font	unused
 * @uiDefault Panel.background			Color	only used if opaque
 * @uiDefault Panel.foreground			Color
 * @uiDefault Panel.border				Border
 *
 * @author Karl Tauber
 */
public class FlatPanelUI
	extends BasicPanelUI
	implements StyleableUI
{
	private final boolean shared;
	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatPanelUI.class, () -> new FlatPanelUI( true ) )
			: new FlatPanelUI( false );
	}

	/** @since 2 */
	protected FlatPanelUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		propertyChangeListener = FlatStylingSupport.createPropertyChangeListener(
			c, () -> stylePropertyChange( (JPanel) c ), null );
		c.addPropertyChangeListener( propertyChangeListener );

		installStyle( (JPanel) c );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		c.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;

		oldStyleValues = null;
	}

	private void stylePropertyChange( JPanel c ) {
		if( shared && FlatStylingSupport.hasStyleProperty( c ) ) {
			// unshare component UI if necessary
			// updateUI() invokes installStyle() from installUI()
			c.updateUI();
		} else
			installStyle( c );
		c.revalidate();
		c.repaint();
	}

	/** @since 2 */
	protected void installStyle( JPanel c ) {
		try {
			applyStyle( c, FlatStylingSupport.getResolvedStyle( c, "Panel" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( JPanel c, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( c, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( JPanel c, String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, c, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}
}
