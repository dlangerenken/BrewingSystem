package se.brewingsystem.android.modules;

import com.google.inject.Binder;
import com.google.inject.Module;

import se.brewingsystem.android.network.NetworkCommunicationMock;
import se.brewingsystem.android.utilities.IMessageHelper;
import se.brewingsystem.android.utilities.MessageHelper;
import se.brewingsystem.android.network.INetworkCommunication;


/**
 * Created by Daniel on 18.12.2014.
 */
public class TestModule implements Module {

    /**
     * Configure.
     *
     * @param binder the binder
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(INetworkCommunication.class).to(NetworkCommunicationMock.class);
        binder.bind(IMessageHelper.class).to(MessageHelper.class);
    }
}
