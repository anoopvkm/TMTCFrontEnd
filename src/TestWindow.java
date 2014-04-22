import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.layout.FillLayout;
import swing2swt.layout.FlowLayout;


public class TestWindow extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TestWindow(Composite parent, int style) {
		super(parent, style);
		setLayout(null);
		
		Label lblSelectVirtualChannel = new Label(this, SWT.NONE);
		lblSelectVirtualChannel.setBounds(5, 8, 209, 17);
		lblSelectVirtualChannel.setText("Select Virtual Channel to replay");
		
		Button btnVc = new Button(this, SWT.RADIO);
		btnVc.setBounds(10, 41, 53, 24);
		btnVc.setText("VC0");
		
		Button btnVc_1 = new Button(this, SWT.RADIO);
		btnVc_1.setBounds(71, 41, 53, 24);
		btnVc_1.setText("VC1");
		
		Button btnVc_2 = new Button(this, SWT.RADIO);
		btnVc_2.setBounds(130, 41, 61, 24);
		btnVc_2.setText("VC2");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
