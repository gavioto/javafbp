package com.jpmorrsn.fbp.examples.components;

	import com.jpmorrsn.fbp.engine.Component;
	import com.jpmorrsn.fbp.engine.ComponentDescription;
	import com.jpmorrsn.fbp.engine.InPort;
	import com.jpmorrsn.fbp.engine.InputPort;
	import com.jpmorrsn.fbp.engine.OutPort;
	import com.jpmorrsn.fbp.engine.OutputPort;
	import com.jpmorrsn.fbp.engine.Packet;


	/**
	 * This is is an ad hoc check program, checking that the IPs within each 
	 * substream are in descending order, and the right number in each substream - 
	 * assuming they were generated by GenSS...
	 */
	@ComponentDescription("Check IP sequence within substreams")
	@OutPort(value = "OUT", optional = true)
	@InPort("IN")
	public class CheckSequenceWithinSubstreams extends Component {

	  static final String copyright = "Copyright 2007, 2015, J. Paul Morrison.  At your option, you may copy, "
	      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
	      + "based on the Everything Development Company's Artistic License.  A document describing "
	      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
	      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	  private InputPort inport;

	  private OutputPort outport;

	@Override
	protected void execute() {

		Packet p;
		int seq = -2;		
		int count = 0;

		while (null != (p = inport.receive())) {
			if (p.getType() == Packet.OPEN) {
				if (seq != -2) {
					System.out.println("Stream out of sequence - case 1");
					return;
				}
				seq = -1;
				count = 5;
				
			} else if (p.getType() == Packet.CLOSE) {
				if (seq < 0) {
					System.out.println("Stream out of sequence - case 2");
					return;
				}
				if (count != 0) {
					System.out.println("Wrong number of IPs in substream");
					return;
				}
				seq = -2;	
				
			} else {
				String s = (String) p.getContent();
				int i = s.indexOf("abcd");
				int j = Integer.parseInt(s.substring(0, i));
				if (seq == -1) {
					//System.out.println("1st of substream " + j + ": " + s);
					seq = j;
				}
				else {
					if (j != seq - 1) {
						System.out.println("Stream out of sequence - case 3");
						return;
					}
					seq = j;					
				}
				count--;
			}

			if (outport.isConnected())
				outport.send(p);
			else 
				drop(p);
		}
	}

	  @Override
	  protected void openPorts() {

	    inport = openInput("IN");

	    outport = openOutput("OUT");

	  }
	}

 
