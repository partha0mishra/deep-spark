/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.stratio.deep.core.extractor.client;


import com.stratio.deep.commons.config.ExtractorConfig;
import com.stratio.deep.commons.extractor.actions.*;
import com.stratio.deep.commons.extractor.response.*;
import com.stratio.deep.commons.rdd.IExtractor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.spark.Partition;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExtractorClientHandler<T> extends SimpleChannelInboundHandler<Response> implements
        IExtractor<T> {

    // Stateful properties
    private volatile Channel channel;

    private final BlockingQueue<Response> answer = new LinkedBlockingQueue<Response>();

    public ExtractorClientHandler() {
        super(true);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext
     * , java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        answer.add(msg);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.stratio.deep.rdd.IDeepRDD#getPartitions(org.apache.spark.broadcast.Broadcast, int)
     */
    @Override
    public Partition[] getPartitions(ExtractorConfig<T> config) {

        GetPartitionsAction<T> getPartitionsAction = new GetPartitionsAction<>(config);

        channel.writeAndFlush(getPartitionsAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return ((GetPartitionsResponse) response).getPartitions();
    }

    @Override
    public void close() {
        CloseAction closeAction = new CloseAction();

        channel.writeAndFlush(closeAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return;
    }


    @Override
    public boolean hasNext() {
        HasNextAction hasNextAction = new HasNextAction<>();

        channel.writeAndFlush(hasNextAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return ((HasNextResponse) response).getData();
    }

    @Override
    public T next() {
        NextAction<T> nextAction = new NextAction<>();

        channel.writeAndFlush(nextAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return ((NextResponse<T>) response).getData();
    }


    @Override
    public void initIterator(Partition dp, ExtractorConfig<T> config) {
        InitIteratorAction<T> initIteratorAction = new InitIteratorAction<>(dp, config);

        channel.writeAndFlush(initIteratorAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return;
    }

    @Override
    public IExtractor<T> getExtractorInstance(ExtractorConfig<T> config) {
        ExtractorInstanceAction<T> instanceAction = new ExtractorInstanceAction<>(config);

        channel.writeAndFlush(instanceAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return ((ExtractorInstanceResponse<T>) response).getData();
    }

    @Override
    public void saveRDD(T t) {
        SaveAction<T> saveAction = new SaveAction<>(t);

        channel.writeAndFlush(saveAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return ;
    }

    @Override
    public void initSave(ExtractorConfig<T> config, T first) {
        InitSaveAction<T> initSaveAction = new InitSaveAction<>(config, first);

        channel.writeAndFlush(initSaveAction);

        Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return;
    }




}
